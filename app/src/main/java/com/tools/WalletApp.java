package com.tools;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletApp extends Application {
    public static final String TAG = "WalletApp";
    private static WalletApp instance;

    private WalletApp() {
        instance = this;
    }

    public static WalletApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
