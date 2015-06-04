package com.jtxdriggers.android.glass.glasslauncher.task;

import com.jtxdriggers.android.glass.glasslauncher.callback.DownloadCallBack;
import com.jtxdriggers.android.glass.glasslauncher.exception.ConnectionFailException;
import com.jtxdriggers.android.glass.glasslauncher.exception.DownloadFailException;
import com.jtxdriggers.android.glass.glasslauncher.operation.FileOperation;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.TimerTask;

/**
 * Created by roy on 2015/3/26.
 */
public class DownloadTask extends TimerTask {

    DownloadCallBack callback = null;
    URL url;

    public DownloadTask(URL url, DownloadCallBack callback){
        this.callback = callback;
        this.url = url;
    }
    @Override
    public void run() {
        try {
            callback.start();
            FileOperation.downloadMusicOperation(url, "music1", callback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (DownloadFailException e) {
            e.printStackTrace();
        }
    }
}
