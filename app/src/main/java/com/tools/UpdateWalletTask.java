package com.tools;

import android.os.AsyncTask;
import android.util.Log;

import com.mybitcoinwallet.WalletActivity;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.WalletFragment;

import org.bitcoinj.kits.WalletAppKit;

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

    @Override
    protected Object doInBackground(Object[] params) {
        walletState = WalletState.getInstantce();
        kit = walletState.getKit();

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
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
