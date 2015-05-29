package com.tasks;

import android.util.Log;

import com.tools.WalletState;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.kits.WalletAppKit;

/**
 * Created by Mihail on 5/28/15.
 */
public class TransactionProcessor extends Thread {
    public static final String TAG = "TransactionCommitter";
    private WalletState walletState;
    private WalletAppKit kit;
    private Wallet wallet;

    public TransactionProcessor() {
        Log.d(TAG, "In the constructor of the transactionProcessor!");
        walletState = WalletState.getInstance();
        kit = walletState.getKit();
        wallet = kit.wallet();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Processing all the pending transactions!");

            processTransactions();
        }

    }

    public void processTransactions() {
        for (Transaction t : wallet.getPendingTransactions()) {

        }
    }
}
