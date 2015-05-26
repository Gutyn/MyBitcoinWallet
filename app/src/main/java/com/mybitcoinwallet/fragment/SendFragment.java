package com.mybitcoinwallet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mybitcoinwallet.R;
import com.tools.WalletState;


/**
 * Created by Mihail on 5/23/15.
 */
public class SendFragment extends Fragment {
    private static final String TAG = "SendFragment";
    public static final String ARG_WALLET = "send";
    private Button sendButton;
    private EditText destinationAddress;
    private EditText amount;
    private WalletState walletState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendButton = (Button) getView().findViewById(R.id.send_button);
        destinationAddress = (EditText) getView().findViewById(R.id.destination_address_editText);
        amount = (EditText) getView().findViewById(R.id.amount_text_editText);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_send, container, false);

        return rootView;
    }

    public double getAmount() {
        double result = Double.parseDouble(amount.getText().toString());
        return result;

    }

}