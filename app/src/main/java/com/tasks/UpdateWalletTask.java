package com.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.mybitcoinwallet.WalletActivity;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.WalletFragment;
import com.tools.WalletState;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;

import java.io.File;

/**
 * Created by Mihail on 5/27/15.
 */
public class UpdateWalletTask extends AsyncTask {
    public static final String TAG = "UpdateWalletTask";
    private WalletState walletState;
    private String address;
    private WalletAppKit kit;
    private MyPagerAdapter pagerAdapter;
    private WalletFragment walletFragment;
    private Coin current;
    private File walletFile;
    private String walletPrefix = "myWallet";
    private Wallet wallet;

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d(TAG, "The wallet updater is started!");
        walletState = WalletState.getInstance();
        kit = walletState.getKit();
        wallet = kit.wallet();
        current = wallet.getBalance(Wallet.BalanceType.ESTIMATED);
        //refresh the wallet
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.e(TAG, "UpdateWalletTask UI update!!!!");
        pagerAdapter = WalletActivity.getMainActivity().getPagerAdapter();
        walletFragment = pagerAdapter.getWalletFragment();
        walletFragment.setBalanceText(current.toFriendlyString());
        walletFragment.setWallet_progressBar_visibility();
    }
}
