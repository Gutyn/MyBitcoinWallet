package com.tools;

import android.app.Application;
import android.app.backup.BackupManager;
import android.os.AsyncTask;
import android.util.Log;

import com.mybitcoinwallet.WalletActivity;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.WalletFragment;
import com.mybitcoinwallet.listeners.WalletListener;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
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
    private String testPass;

    private WalletState() {
        app = WalletActivity.getMainActivity().getApplication();
    }

    public void initiate() {

        Log.d(TAG, "Synchronizing the kit");
        File file = new File(app.getFilesDir().getAbsolutePath());
        Log.d(TAG, "The file is : " + file);
        kit = new WalletAppKit(network, file, filePrefix);
        kit.startAsync();
        new BackgroundTask().execute();
//        new BackgroundTask2().execute();
        Log.d(TAG, "Running done!");
    }

    public static WalletState getInstantce() {
        if (walletState == null) {
            walletState = new WalletState();
        }
        return walletState;
    }

    public void receiveMoney(Transaction tx) {
        try {
            kit.wallet().commitTx(tx);
            System.out.println(tx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BackgroundTask extends AsyncTask {
        public static final String TAG = "BackgroundTask";

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(TAG, "Starting doInBackground");
            try {
                kit.awaitRunning();
                walletListener = new WalletListener();
                kit.wallet().addEventListener(walletListener);

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
            address = kit.wallet().freshReceiveAddress().toString();
            Log.i(TAG, "onPostExecute! finally and the Key is: " + address);
            pagerAdapter = WalletActivity.getMainActivity().getPagerAdapter();
            walletFragment = pagerAdapter.getWalletFragment();
            walletFragment.setBalanceText(kit.wallet().getBalance().toFriendlyString());
            walletFragment.setmTextView(address.toString());
            walletFragment.setWallet_progressBar_visibility();

        }
    }

    public class BackgroundTask2 extends AsyncTask {
        public static final String TAG = "BackgroundTask";

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(TAG, "Starting doInBackground2");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.i(TAG, "The wallet: " + kit.wallet());
            address = kit.wallet().freshReceiveAddress().toString();
            Log.i(TAG, "onPostExecute! finally and the Key is: " + address);
            pagerAdapter = WalletActivity.getMainActivity().getPagerAdapter();
            walletFragment = pagerAdapter.getWalletFragment();
            walletFragment.setBalanceText(kit.wallet().getBalance().toFriendlyString());
            walletFragment.setmTextView(address.toString());
            walletFragment.setWallet_progressBar_visibility();
        }

    }

    public WalletAppKit getKit() {
        return kit;
    }
}