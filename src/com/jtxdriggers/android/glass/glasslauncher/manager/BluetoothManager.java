package com.jtxdriggers.android.glass.glasslauncher.manager;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jtxdriggers.android.glass.glasslauncher.dialog.BluetoothInputDialog;
import com.jtxdriggers.android.glass.glasslauncher.model.MessageModel;
import com.jtxdriggers.android.glass.glasslauncher.service.BluetoothCommService;

/**
 * Created by roy on 2015/4/16.
 */
public class BluetoothManager {
    Context ctx = null;
    int status = 0;
    static BluetoothCommService mBTService = null;

    static BluetoothInputDialog btDialog = null;

    public BluetoothManager(Context ctx){
        this.ctx = ctx;
    }
    public static void send(MessageModel msg, String data){
        receiveData(data);
    }

    public static void receiveData(String text){
        Log.d("glass", "BluetoothManager:receiveData:" + text+","+btDialog);
        if(btDialog!=null) {
            btDialog.setText(text);
        }
    }
    public static void setCurrentBluetoothDialog(BluetoothInputDialog btDialog){

        BluetoothManager.btDialog = btDialog;
    }
    public static void setBluetoothService(BluetoothCommService mBTService){
        BluetoothManager.mBTService = mBTService;
    }
    public static BluetoothCommService getBluetoothService(){
        return mBTService;
    }
}
