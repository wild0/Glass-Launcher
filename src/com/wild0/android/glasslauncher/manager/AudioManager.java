package com.wild0.android.glasslauncher.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by roy on 2015/2/17.
 */
public class AudioManager {

    //AudioManager audio = null;
    static Context context = null;
    public void initial(Context context) {
        AudioManager.context = context;
        //AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //audio.playSoundEffect(Sounds.TAP);
    }
    public void play(Uri uri){

        MediaPlayer mp = MediaPlayer.create(context, uri);
    }
    public static void add(Context ctx, File audiofile){
        // add new file to your media library
        ContentResolver contentResolver = ctx.getContentResolver();
        String mimeType = contentResolver.getType(Uri.fromFile(audiofile));

        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());


        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        // Notifiy the media application on the device
        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }
}
