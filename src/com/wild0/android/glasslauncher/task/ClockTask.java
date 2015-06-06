package com.wild0.android.glasslauncher.task;

import com.wild0.android.glasslauncher.callback.TickCallBack;

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
