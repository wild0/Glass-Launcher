package com.jtxdriggers.android.glass.glasslauncher.task;

import com.jtxdriggers.android.glass.glasslauncher.callback.DownloadCallBack;
import com.jtxdriggers.android.glass.glasslauncher.callback.TickCallBack;
import com.jtxdriggers.android.glass.glasslauncher.exception.ConnectionFailException;
import com.jtxdriggers.android.glass.glasslauncher.exception.DownloadFailException;
import com.jtxdriggers.android.glass.glasslauncher.operation.FileOperation;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.TimerTask;

/**
 * Created by roy on 2015/4/6.
 */
public class ClockTask extends TimerTask {
    TickCallBack callback = null;
    boolean enable  = true;
    public ClockTask( TickCallBack callback){
        this.callback = callback;


    }

    public void run() {
        try {
            while(enable){
                callback.change();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        finally{

        }
    }
    public void stop(){
        enable = false;
    }

}
