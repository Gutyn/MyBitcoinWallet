package com.tools;

import android.app.Application;
import android.app.backup.BackupManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.BlockStore;
import com.google.bitcoin.core.BlockStoreException;
import com.google.bitcoin.core.BoundedOverheadBlockStore;
import com.google.bitcoin.core.DnsDiscovery;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.IrcDiscovery;
import com.google.bitcoin.core.NetworkConnection;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerDiscovery;
import com.google.bitcoin.core.PeerDiscoveryException;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.mybitcoinwallet.WalletActivity;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.WalletFragment;
import com.mybitcoinwallet.listeners.WalletListener;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletState {
    public static final String TAG = "WalletState";
    private boolean TEST_MODE = true;
    private String filePrefix = TEST_MODE ? "testnet" : "prodnet";
    private NetworkParameters network =
            TEST_MODE ? NetworkParameters.testNet() : NetworkParameters.prodNet();
    private Wallet wallet;
    boolean walletShouldBeRebuilt = false;
    private Address address;
    static final Object[] walletFileLock = new Object[0];
    private MyPagerAdapter pagerAdapter;
    private static WalletState walletState;
    private File keychainFile;
    private BlockStore blockStore = null;
    private BlockChain blockChain;
    static final Object[] connectedPeersLock = new Object[0];
    private ArrayList<Peer> connectedPeers = new ArrayList<Peer>();
    private BackupManager backupManager;
    private File walletFile;
    private WalletFragment walletFragment;
    private int remaining;
    private ArrayList<InetSocketAddress> isas = new ArrayList<InetSocketAddress>();
    private PeerDiscovery peerDiscovery;
    private Application app;

    private WalletState() {
        app = WalletActivity.getMainActivity().getApplication();
    }

    public void initiate() {
        new BackgroundTask().execute();
    }

    public static WalletState getInstantce() {
        if (walletState == null) {
            walletState = new WalletState();
        }
        return walletState;
    }

    public Wallet getWallet(NetworkParameters netParams) {
        try {
            wallet = Wallet.loadFromFile(walletFile);
            Log.d(TAG, "Found wallet file to load");
        } catch (Exception e) {
            e.printStackTrace();
            wallet = new Wallet(netParams);
            Log.d(TAG, "Created new wallet...now attempting to reset prior keys");

            //load any previous keys in case this IOException was due to a serialization change of a previous wallet
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keychainFile));
                @SuppressWarnings("unchecked")
                ArrayList<ECKey> keys = (ArrayList<ECKey>) ois.readObject();
                for (ECKey key : keys) {
                    wallet.keychain.add(key);
                }
                walletShouldBeRebuilt = true;
            } catch (Exception e2) {
                Log.d(TAG, "No prior keys found, a brand new wallet!");
                ECKey k = new ECKey();
                Log.d(TAG, "Added a new key: " + k);
                wallet.keychain.add(k);
            }
            saveWallet();
        } catch (StackOverflowError e) {
            //couldn't deserialize the wallet - maybe it was saved in a previous version of bitcoinj
            e.printStackTrace();
        }

        return wallet;
    }

    public class BackgroundTask extends AsyncTask {
        public static final String TAG = "BackgroundTask";

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(TAG, "Starting doInBackground");
            try {
                Log.d(TAG, "Started");
                keychainFile = new File(app.getFilesDir(), filePrefix + ".keychain");
                walletFile = new File(app.getFilesDir(), filePrefix + ".wallet");
                wallet = getWallet(network);
                wallet.addEventListener(new WalletListener());
                if (TEST_MODE) {
                    peerDiscovery = new IrcDiscovery("#bitcoin");
                    Log.i(TAG, "peerDiscovery: " + peerDiscovery);
                } else {
                    peerDiscovery = new DnsDiscovery(network);
                }
                Log.d(TAG, "Reading block store from disk");
                try {
                    File file = new File(app.getExternalFilesDir(null), filePrefix
                            + ".blockchain");
                    if (!file.exists()) {
                        Log.d(TAG, "Copying initial blockchain from assets folder");
                        InputStream is = null;
                        try {
                            is = app.getAssets().open(
                                    filePrefix + ".blockchain");
                            IOUtils.copy(is, new FileOutputStream(file));
                        } catch (IOException e) {
                            Log.d(TAG,
                                    "Couldn't find initial blockchain in assets folder...starting from scratch");
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    blockStore = new BoundedOverheadBlockStore(network, file);
                    blockChain = new BlockChain(network, wallet, blockStore);
                    downloadBlockChain();
                    connectToLocalPeers();
                    Log.d(TAG, "Ending background task");
                    return true;
                } catch (BlockStoreException bse) {
                    throw new Error("Couldn't store block.");
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            ECKey key = wallet.keychain.get(0);
            address = key.toAddress(network);
            Log.i(TAG, "onPostExecute! finally and the Key is: " + address);
            pagerAdapter = WalletActivity.getMainActivity().getPagerAdapter();
            walletFragment = pagerAdapter.getWalletFragment();
            walletFragment.setBalanceText(Utils.bitcoinValueToFriendlyString
                    (wallet.getBalance(Wallet.BalanceType.ESTIMATED)));
            walletFragment.setmTextView(address.toString());
            walletFragment.setWallet_progressBar_visibility();

        }

        private void downloadBlockChain() {
            Log.d(TAG, "Downloading block chain");

            remaining = 0;
            boolean done = false;
            while (!done) {
                ArrayList<InetSocketAddress> isas = discoverPeers();
                if (isas.size() == 0) {
                    // not connected to internet
                    done = true;
                } else {
                    for (InetSocketAddress isa : isas) {
                        if (blockChainDownloadSuccessful(isa)) {
                            done = true;
                            break;
                        } else {
                            // remove and try next one
                            removeBadPeer(isa);
                        }
                    }
                }
            }

        }

        private boolean blockChainDownloadSuccessful(InetSocketAddress isa) {
            NetworkConnection conn = createNetworkConnection(isa);
            if (conn == null)
                return false;

            Peer peer = new Peer(network, conn, blockChain);
            peer.start();
            try {
                Log.d(TAG, "Starting download from new peer");
                peer.startBlockChainDownload();
                Log.d(TAG, "Download from new peer done");
            } catch (IOException e) {
                Log.d(TAG, "IOException in blockChainDownloadSuccessful");
                e.printStackTrace();
            } finally {
                // always calls this even if we return above
                peer.disconnect();
            }

            return true;
        }

        private NetworkConnection createNetworkConnection(final InetSocketAddress isa) {
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<NetworkConnection> task = new Callable<NetworkConnection>() {
                public NetworkConnection call() {
                    NetworkConnection conn = null;
                    try {
                        conn = new NetworkConnection(isa.getAddress(), network,
                                blockStore.getChainHead().getHeight(), 8000);
                        Log.e(TAG, " after conn assignment");
                    } catch (Exception e) {
                    }
                    return conn;
                }
            };
            Future<NetworkConnection> future = executor.submit(task);
            NetworkConnection result = null;
            try {
                result = future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                future.cancel(true);
            }
            return result;
        }

        /* connect to local peers (minimum of 3, maximum of 8) */
        private synchronized void connectToLocalPeers() {
            Log.d(TAG, "Connecting to local peers");
            synchronized (connectedPeersLock) {
                // clear out any which have disconnected
                if (connectedPeers.size() < 3) {
                    for (InetSocketAddress isa : discoverPeers()) {
                        NetworkConnection conn = createNetworkConnection(isa);
                        if (conn == null) {
                            removeBadPeer(isa);
                            Log.d(TAG, "removed peer");
                        } else {
                            Peer peer = new Peer(network, conn, blockChain);
                            peer.start();
                            connectedPeers.add(peer);
                            if (connectedPeers.size() >= 8)
                                break;
                        }
                    }
                }
            }
        }

    }

    public Wallet getReadyWallet() {
        if (wallet != null) {
            Log.i(TAG, "The ready wallet was returned");
            return wallet;
        }
        return getWallet(network);
    }

    public void saveWallet() {
        synchronized (WalletState.walletFileLock) {
            Log.d(TAG, "Saving wallet");
            try {
                wallet.saveToFile(walletFile);
            } catch (IOException e) {
                throw new Error("Can't save wallet file.");
            }

            // save keys also in case we need to rebuild wallet later (serialization changes)
            ObjectOutputStream keychain;
            try {
                keychain = new ObjectOutputStream(new FileOutputStream(keychainFile));
                keychain.writeObject(wallet.keychain);
                keychain.flush();
                keychain.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Can't save keychain file.");
            }
        }
        Log.d(TAG, "Notifying BackupManager that data has changed. Should backup soon.");
        backupManager.dataChanged();
    }

    public synchronized void removeBadPeer(InetSocketAddress isa) {
        Log.d(TAG, "removing bad peer");
        isas.remove(isa);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<InetSocketAddress> discoverPeers() {
        if (isas.size() == 0) {
            Log.i(TAG, "Discovering peers...");
            try {
                isas.addAll(Arrays.asList(peerDiscovery.getPeers()));
                Collections.shuffle(isas); // try different order each time
            } catch (PeerDiscoveryException e) {
                Log.d(TAG, "Couldn't discover peers.", e);
                e.printStackTrace();
            }
        }
        Log.d(TAG, "discoverPeers returning " + isas.size() + " peers");
        // shallow clone to prevent concurrent modification exceptions
        return (ArrayList<InetSocketAddress>) isas.clone();
    }

}
