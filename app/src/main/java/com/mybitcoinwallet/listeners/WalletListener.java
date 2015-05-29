package com.mybitcoinwallet.listeners;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.mybitcoinwallet.fragment.WalletFragment;
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
    private WalletState walletState;
    private MyPagerAdapter pagerAdapter;
    private WalletFragment walletFragment;

    public WalletListener() {
        walletState = WalletState.getInstance();
        pagerAdapter = walletState.getPagerAdapter();
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
        Log.d(TAG, "-----> coins received: " + transaction.getHashAsString());
        Log.d(TAG, "received: " + transaction.getValueSentToMe(wallet));
        Log.d(TAG, "The balance is: " + wallet.getBalance().toString());
        Log.d(TAG, "The new balance is: " + coin1.toFriendlyString());

        Futures.addCallback(transaction.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                // "result" here is the same as "tx" above, but we use it anyway for clarity.
                walletState.processTransaction(result);
                walletState.updateUI();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failure to receive money!");
            }
        });
    }

    @Override
    public void onCoinsSent(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
        Log.d(TAG, "coins sent");


        Futures.addCallback(transaction.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>() {

            @Override
            public void onSuccess(Transaction result) {
                // "result" here is the same as "tx" above, but we use it anyway for clarity.
                walletState.processTransaction(result);
                walletState.updateUI();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failure to receive money!");
            }
        });
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
        Log.e(TAG, "Wallet changed!!!");
        walletState.updateUI();
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
