package com.mybitcoinwallet.listeners;

import android.util.Log;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletEventListener;
import com.tools.WalletState;

import java.math.BigInteger;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletListener extends WalletEventListener {
    public static final String TAG = "WalletListener";
    private WalletState walletState;

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction transaction, BigInteger bigInteger, BigInteger bigInteger1) {
        super.onCoinsReceived(wallet, transaction, bigInteger, bigInteger1);
        Log.d(TAG, "-----> coins resceived: " + transaction.getHashAsString());
        Log.d(TAG, "received: " + transaction.getValueSentToMe(wallet));
        walletState = WalletState.getInstantce();
        wallet = walletState.getReadyWallet();
//        walletState.refreshWallet();
        Log.d(TAG, "The balance is: " + wallet.getBalance().toString());
//        walletState.reveicePayment(tx);
    }

    @Override
    public void onDeadTransaction(Transaction transaction, Transaction transaction1) {
        super.onDeadTransaction(transaction, transaction1);
    }

    @Override
    public void onReorganize() {
        super.onReorganize();
    }
}
