package com.mybitcoinwallet;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.mybitcoinwallet.adapter.MyPagerAdapter;
import com.tasks.UpdateWalletTask;
import com.tools.WalletState;

/**
 * Created by Mihail on 5/23/15.
 */
public class WalletActivity extends FragmentActivity {
    public static final String TAG = "WalletActivity";
    private WalletState walletState;
    private static WalletActivity walletActivity;
    private MyPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    public WalletActivity() {
        walletActivity = this;
    }

    public static WalletActivity getMainActivity() {
        return walletActivity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        walletState = WalletState.getInstance();

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(viewPager);
        walletState.initiate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_menu_item) {
            new UpdateWalletTask().execute();
            Toast.makeText(this, "Wallet refreshed!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MyPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }


}
