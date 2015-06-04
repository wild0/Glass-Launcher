package com.jtxdriggers.android.glass.glasslauncher.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jtxdriggers.android.glass.glasslauncher.R;

import java.io.IOException;

/**
 * Created by roy on 2015/2/25.
 */
public class PlayAudioService  extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    public static final String ACTION_PLAY = "com.orangice.glass.action.PLAY";
    public static final String ACTION_PAUSE = "com.orangice.glass.action.PAUSE";
    private static final String ACTION_RESUME = "com.orangice.glass.action.RESUME";
    public static final String ACTION_STOP = "com.orangice.glass.action.STOP";
    static MediaPlayer mMediaPlayer = null;

    public static boolean isPlaying() {
        try {
            return mMediaPlayer.isPlaying();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public void onCreate() {
        //mMediaPlayer = MediaPlayer.create(this, R.raw.test);// raw/test.mp3
        //mMediaPlayer.setOnCompletionListener(this);
        //this.getApplicationContext().get
        //String playUri = i;
        //Uri uri = Uri.parse(playUri);
        //mMediaPlayer = MediaPlayer.create(this, uri);
        mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
        mMediaPlayer = MediaPlayer.create(this, R.raw.music); // initialize it here
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        //mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        Log.d("glass","Action:"+intent.getAction());

        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer.start();
            //mMediaPlayer = MediaPlayer.create(this, R.raw.music); // initialize it here
            //mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //    public void onPrepared(MediaPlayer mp) {
                    //ToggleButton playButton = (ToggleButton) findViewById(R.id.playToggleButton);
                    //playButton.setClickable(true);
            //        mp.start();
            //   }
            //});

            //mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        }
        else if(intent.getAction().equals(ACTION_STOP)){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public class PlayAudioServiceBinder extends Binder {
        public PlayAudioService getService() {
            return PlayAudioService.this;
        }
    }

    private final IBinder playAudioBinder = new PlayAudioServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return playAudioBinder;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    public void onDestroy() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        Log.v("SIMPLESERVICE", "onDestroy");
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        stopSelf();
    }
}
