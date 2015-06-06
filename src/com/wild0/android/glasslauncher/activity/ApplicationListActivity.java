package com.wild0.android.glasslauncher.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.wild0.android.glasslauncher.GlassLauncherService;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.model.ApplicationInfo;

/**
 * Created by roy on 2015/4/25.
 */
public class ApplicationListActivity extends Activity {


    public static final String UPDATE_RECEIVER = "com.justindriggers.android.glass.glasslauncher.UPDATE_RECEIVER";

    private ApplicationsUpdateReceiver mApplicationsReceiver = new ApplicationsUpdateReceiver();
    private ArrayList<ApplicationInfo> mApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(mApplicationsReceiver, new IntentFilter(UPDATE_RECEIVER));

        mApplications = GlassLauncherService.getApplications();

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        openOptionsMenu();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mApplicationsReceiver);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mApplications.isEmpty()) {
            MenuItem item = menu.add(Menu.NONE, -1, Menu.NONE, "No Apps Found");
            item.setIcon(R.drawable.ic_warning_50);
        } else {
            ApplicationInfo app;
            MenuItem item;

            for (int i = 0; i < mApplications.size(); i++) {
                app = mApplications.get(i);
                item = menu.add(Menu.NONE, i, i+1, app.title);
                item.setIcon(app.icon);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == -1) {
            return true;
        }

        Intent intent = mApplications.get(item.getItemId()).intent;
        startActivity(intent);

        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        finish();
    }

    private class ApplicationsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            startActivity(new Intent(context, ApplicationListActivity.class));
        }
    }
}
