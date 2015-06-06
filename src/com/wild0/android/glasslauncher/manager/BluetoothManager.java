package com.wild0.android.glasslauncher.manager;

import android.content.Context;
import android.util.Log;

import com.wild0.android.glasslauncher.dialog.BluetoothInputDialog;
import com.wild0.android.glasslauncher.model.MessageModel;
import com.wild0.android.glasslauncher.service.BluetoothCommService;

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
