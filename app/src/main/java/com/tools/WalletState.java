package com.tools;

import android.app.Application;
import android.app.backup.BackupManager;
import android.os.AsyncTask;
import android.util.Log;

import com.mybitcoinwallet.WalletActivity;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.WalletFragment;
import com.mybitcoinwallet.listeners.WalletListener;
import com.tasks.UpdateWalletTask;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import java.io.File;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletState {
    public static final String TAG = "WalletState";
    private boolean TEST_MODE = true;
    private String filePrefix = TEST_MODE ? "testwallet" : "prodwallet";
    private NetworkParameters network =
            TEST_MODE ? TestNet3Params.get() : MainNetParams.get();
    static final Object[] walletFileLock = new Object[0];
    private MyPagerAdapter pagerAdapter;
    private static WalletState walletState;
    static final Object[] connectedPeersLock = new Object[0];
    private BackupManager backupManager;
    private WalletFragment walletFragment;
    private WalletAppKit kit;
    private String address;
    private Application app;
    private WalletListener walletListener;
    private Wallet wallet;

    private WalletState() {
        app = WalletActivity.getMainActivity().getApplication();
    }

    public void initiate() {

        Log.d(TAG, "Synchronizing the kit");
        File file = new File(app.getFilesDir().getAbsolutePath());
        Log.d(TAG, "The file is : " + file);
        kit = new WalletAppKit(network, file, filePrefix) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called,
                // as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering
                // in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeychainSize() < 1)
                    wallet().importKey(new ECKey());
            }
        };
        Log.i(TAG, "Before startAsync");
        kit.setAutoSave(true);
        kit.startAsync();
        new BackgroundTask().execute();
    }

    public static WalletState getInstance() {
        if (walletState == null) {
            walletState = new WalletState();
            Log.e(TAG, "New Wallet State CreateD!!!!!");
        }
        return walletState;
    }

    public class BackgroundTask extends AsyncTask {
        public static final String TAG = "BackgroundTask";

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(TAG, "Starting doInBackground");
            try {
                kit.awaitRunning();
//                new TransactionProcessor().start();
                wallet = kit.wallet();
                walletListener = new WalletListener();
                wallet.addEventListener(walletListener);
                Log.d(TAG, "Done synchronizing the kit");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error synchronizing the kit");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.i(TAG, "The wallet: " + kit.wallet());
            if (wallet.getWatchedAddresses().isEmpty()) {
                wallet.addWatchedAddress(wallet.freshReceiveAddress());
            }
            address = wallet.getWatchedAddresses().get(0).toString();

            Log.i(TAG, "onPostExecute! finally and the Key is: " + address);
            pagerAdapter = WalletActivity.getMainActivity().getPagerAdapter();
            walletFragment = pagerAdapter.getWalletFragment();
            walletFragment.setmTextView(address.toString());
            walletFragment.setWallet_progressBar_visibility();
            updateUI();
        }
    }

    public void processTransaction(Transaction tx) {
        Log.d(TAG, "Trying to commit the transaction!");
        try {
            wallet.commitTx(tx);
        } catch (VerificationException e) {
            Log.e(TAG, "Failed to commit the transaction!", e);
        }
    }

    public void updateUI() {
        new UpdateWalletTask().execute();
    }

    public WalletAppKit getKit() {
        return kit;
    }

    public NetworkParameters getParams() {
        return network;
    }

    public MyPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

}