package com.mybitcoinwallet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybitcoinwallet.R;


/**
 * Created by Mihail on 5/23/15.
 */
public class TransactionsFragment extends Fragment {
    public static final String TAG = "TransactionsFragment";
    public static final String ARG_WALLET = "transactions";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_transactions, container, false);
        return rootView;
    }
}