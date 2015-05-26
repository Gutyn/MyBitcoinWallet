package com.mybitcoinwallet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mybitcoinwallet.R;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletFragment extends Fragment {
    public static final String TAG = "WaletFragment";
    public static final String ARG_WALLET = "wallet";   
    private View view;
    private TextView mTextView;
    private TextView balanceText;
    private RelativeLayout wallet_layout;
    private ProgressBar wallet_progressBar;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextView = (TextView) getView().findViewById(R.id.the_address_text);
        balanceText = (TextView) getView().findViewById(R.id.the_balance);
        wallet_layout = (RelativeLayout) getView().findViewById(R.id.wallet_layout);
        wallet_progressBar = (ProgressBar) getView().findViewById(R.id.wallet_progressBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "WalletFragment onCreate()");

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(
                R.layout.fragment_wallet, container, false);
        Log.i(TAG, "onCreateView fragment is done!");
        return view;
    }

    public void setmTextView(String s) {
        mTextView.setText(s);
    }

    public void setBalanceText(String s) {
        balanceText.setText(s);
    }

    public void setWallet_progressBar_visibility() {
        wallet_progressBar.setVisibility(View.GONE);
    }

}