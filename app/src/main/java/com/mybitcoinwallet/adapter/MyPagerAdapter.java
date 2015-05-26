package com.mybitcoinwallet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mybitcoinwallet.fragment.SendFragment;
import com.mybitcoinwallet.fragment.TransactionsFragment;
import com.mybitcoinwallet.fragment.WalletFragment;

/**
 * Created by Mihail on 5/23/15.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Send", "Wallet", "Transactions"};
    private WalletFragment walletFragment;
    private SendFragment sendFragment;
    private TransactionsFragment transactionsFragment;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    // Returns the walletFragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                sendFragment = new SendFragment();
                return sendFragment;
            case 1:
                walletFragment = new WalletFragment();
                return walletFragment;
            case 2:
                transactionsFragment = new TransactionsFragment();
                return transactionsFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public WalletFragment getWalletFragment() {
        return walletFragment;
    }

    public SendFragment getSendFragment() {
        return sendFragment;
    }

    public TransactionsFragment getTransactionsFragment() {
        return transactionsFragment;
    }


}
