package com.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.mybitcoinwallet.WalletActivity;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.SendFragment;
import com.mybitcoinwallet.fragment.WalletFragment;
import com.tools.WalletState;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;

/**
 * Created by Mihail on 5/28/15.
 */
public class SendMoneyTask extends AsyncTask {
    public static final String TAG = "SendMoneyTask";
    private WalletState walletState;
    private String address;
    private WalletAppKit kit;
    private MyPagerAdapter pagerAdapter;
    private WalletFragment walletFragment;
    private SendFragment sendFragment;
    private Coin current;
    private Wallet wallet;
    private boolean success = true;

    @Override
    protected Boolean doInBackground(Object[] params) {
        Log.d(TAG, "The wallet updater is started!");
        walletState = WalletState.getInstance();
        kit = walletState.getKit();
        wallet = kit.wallet();

        try {
            if (params.length == 2) {
                Coin value = Coin.parseCoin((String) params[0]);
                Address to = new Address(walletState.getParams(), (String) params[1]);
                Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
                Log.d(TAG, "coins sent. transaction hash: " + result.tx.getHashAsString());

            } else {
                Log.e(TAG, "Need 2 parameeters!!!!!");
                success = false;
            }
            return true;
        } catch (AddressFormatException e) {
            success = false;
            Log.e(TAG, "Wrong address format", e);
            return false;
        } catch (InsufficientMoneyException e) {
            success = false;
            Log.e(TAG, "InsufficientFunds", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        pagerAdapter = WalletActivity.getMainActivity().getPagerAdapter();
        walletFragment = pagerAdapter.getWalletFragment();
        sendFragment = pagerAdapter.getSendFragment();
//        walletFragment.setBalanceText(wallet.getBalance().toFriendlyString());
//        walletFragment.setWallet_progressBar_visibility();
        if (success) {
            sendFragment.showSuccessMessage();
        } else {
            sendFragment.showFailedMessage();
        }
        walletState.updateUI();
    }
}
