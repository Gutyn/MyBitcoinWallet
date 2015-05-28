package com.mybitcoinwallet.listeners;

import android.util.Log;

import com.tools.UpdateWalletTask;
import com.tools.WalletState;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.core.WalletEventListener;
import org.bitcoinj.script.Script;

import java.util.List;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletListener implements WalletEventListener {
    public static final String TAG = "WalletListener";
    WalletState walletState;

    public WalletListener() {
        walletState = WalletState.getInstantce();
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
        Log.d(TAG, "-----> coins resceived: " + transaction.getHashAsString());
        Log.d(TAG, "received: " + transaction.getValueSentToMe(wallet));
        Log.d(TAG, "The balance is: " + wallet.getBalance().toString());
        walletState.receiveMoney(transaction);
        new UpdateWalletTask().execute();

    }


    @Override
    public void onCoinsSent(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
        Log.d(TAG, "coins sent");
    }

    @Override
    public void onReorganize(Wallet wallet) {

    }

    @Override
    public void onTransactionConfidenceChanged(Wallet wallet, Transaction transaction) {
        Log.d(TAG, "-----> confidence changed: " + transaction.getHashAsString());
        TransactionConfidence confidence = transaction.getConfidence();
        Log.d(TAG, "new block depth: " + confidence.getDepthInBlocks());
    }

    @Override
    public void onWalletChanged(Wallet wallet) {

    }

    @Override
    public void onScriptsAdded(Wallet wallet, List<Script> list) {
        Log.d(TAG, "new script added");
    }

    @Override
    public void onKeysAdded(List<ECKey> list) {
        Log.d(TAG, "new key added");
    }
}
