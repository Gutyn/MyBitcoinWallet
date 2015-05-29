package com.mybitcoinwallet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mybitcoinwallet.R;
import com.tasks.SendMoneyTask;
import com.tools.WalletState;


/**
 * Created by Mihail on 5/23/15.
 */
public class SendFragment extends Fragment {
    private static final String TAG = "SendFragment";
    private Button sendButton;
    private EditText destinationAddress;
    private EditText amount;
    private WalletState walletState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walletState = WalletState.getInstance();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendButton = (Button) getView().findViewById(R.id.send_button);
        destinationAddress = (EditText) getView().findViewById(R.id.destination_address_editText);
        amount = (EditText) getView().findViewById(R.id.amount_text_editText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String destAddress = destinationAddress.getText().toString();
                final String theAmount = amount.getText().toString();
                if (destAddress != null && theAmount != null) {
                    Log.e(TAG, "Starting SendMoneyTask!");
                    SendMoneyTask s = new SendMoneyTask();
                    s.execute(theAmount, destAddress);
                } else {
                    Log.e(TAG, "Could not execute onClick!");
                }

            }
        });

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

    public void showSuccessMessage() {
        Toast.makeText(getActivity(), "Bitcoin sent!", Toast.LENGTH_SHORT).show();
    }

    public void showFailedMessage() {
        Toast.makeText(getActivity(), "Failed to send!!!", Toast.LENGTH_LONG).show();
    }

}