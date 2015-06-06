package com.wild0.android.glasslauncher.callback;

import java.io.File;

/**
 * Created by roy on 2015/2/25.
 */
public abstract class DownloadCallBack {

    public abstract void start();
    public abstract void complete(File downloadedFile);
    public abstract void fail(int code);
    public abstract void updateProgress();
}
