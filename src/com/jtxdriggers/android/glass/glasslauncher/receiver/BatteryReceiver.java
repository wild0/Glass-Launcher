package com.jtxdriggers.android.glass.glasslauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by roy on 2015/5/8.
 */
public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
        int  icon_small= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);
        int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
        boolean  present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
        int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
        int  status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
        String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
        int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);


    }
}
