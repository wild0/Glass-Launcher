package com.jtxdriggers.android.glass.glasslauncher.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.jtxdriggers.android.glass.glasslauncher.model.VideoInstance;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by roy on 2015/2/17.
 */
public class VideoManager {
    ArrayList<VideoInstance> nodes = new ArrayList<VideoInstance>();

    public void add(VideoInstance instance){
        nodes.add(instance);
    }
    public ArrayList<VideoInstance> listInstances(){
        return nodes;
    }
    public VideoInstance remove(int index){
        return nodes.remove(index);
    }
    public void add(Context ctx, File videofile){
        // add new file to your media library
        ContentResolver contentResolver = ctx.getContentResolver();
        String mimeType = contentResolver.getType(Uri.fromFile(videofile));

        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "video" + videofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Audio.Media.DATA, videofile.getAbsolutePath());


        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        // Notifiy the media application on the device
        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }
}
