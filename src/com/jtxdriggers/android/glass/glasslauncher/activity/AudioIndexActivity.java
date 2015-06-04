package com.jtxdriggers.android.glass.glasslauncher.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.jtxdriggers.android.glass.glasslauncher.callback.DownloadCallBack;
import com.jtxdriggers.android.glass.glasslauncher.manager.AudioManager;
import com.jtxdriggers.android.glass.glasslauncher.task.DownloadTask;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;


/**
 * Created by roy on 2015/2/17.
 */
public class AudioIndexActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor>,GestureDetector.BaseListener, GestureDetector.FingerListener {



    //private GestureDetector mTouchDetector;

    private static final int URL_LOADER = 0;
    private Cursor mAudioCursor;
    String TAG = "glass";
    static AudioIndexActivity instance = null;
    GestureDetector mGestureDetector;

    /*
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

        }
    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG,"AudioIndexActivity:onCreate:start");


        instance = this;

        setContentView(R.layout.custom_audio_index_layout);

        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
        //mGestureDetector = new GestureDetector(this)
        //        .setOneFingerScrollListener(this).setTwoFingerScrollListener(this);

        Log.d(TAG,"AudioIndexActivity:onCreate:mGestureDetector:"+mGestureDetector.toString());

    }
    /**
     * Overridden to allow the gesture detector to process motion events that occur anywhere within
     * the activity.
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }
    /**
     * This method includes special behavior to handle SWIPE_DOWN gestures. The first time the user
     * swipes down, we return true so that the user can still see the feedback in the gesture
     * label, and we fade in an instructional tip label. The second time the user swipes down, we
     * return false so that the activity can handle the event and return to the previous activity.
     */
    @Override
    public boolean onGesture(Gesture gesture) {
        //mLastGesture.setText(gesture.name());
        Log.d(TAG, "gesture:" + gesture);



        if(gesture==Gesture.SWIPE_LEFT){
            //Intent videoIntent = new Intent(instance, AudioIndexActivity.class);
           // instance.startActivity(videoIntent);
            Intent cameraIntent = new Intent(instance, CameraActivity.class);
            instance.startActivity(cameraIntent);
            //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
            //instance.startActivity(cameraIntent);
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent videoIntent = new Intent(instance, VideoIndexActivity.class);
            instance.startActivity(videoIntent);
            //overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        else if(gesture==Gesture.TAP){
            instance.openOptionsMenu();
        }
        else if(gesture==Gesture.SWIPE_DOWN){
            finish();
        }
        return true;

        /*
        if (gesture == Gesture.SWIPE_DOWN) {
            if (!mSwipedDownOnce) {
                mSwipeAgainTip.animate().alpha(1.0f);
                mSwipedDownOnce = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Refresh");
        getMenuInflater().inflate(R.menu.audio_index, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {
            case R.id.enter:
                Intent audioListIntent = new Intent(instance, AudioListActivity.class);
                instance.startActivity(audioListIntent);
                return true;
            case R.id.audio_index_download:
                //https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0
                try {
                    URL url = new URL("https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0");
                    DownloadCallBack callback = new DownloadCallBack(){

                        @Override
                        public void start() {

                        }

                        @Override
                        public void complete(File downloadedFile) {
                            Log.d("glass", "complete:"+downloadedFile.getAbsolutePath());
                            AudioManager.add(instance, downloadedFile);
                        }

                        @Override
                        public void fail(int code) {

                        }

                        @Override
                        public void updateProgress() {

                        }
                    };
                    Timer t = new Timer();
                    DownloadTask task = new DownloadTask(url, callback);
                    t.schedule(task, 0);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onFingerCountChanged(int i, int i2) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.d(TAG, "onCreateLoader:" + loaderId);
        switch(loaderId) {
            case URL_LOADER:
                String[] proj = { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE };

                //long bucketId = getIntent().getLongExtra(EXTRA_MOVIE_BUCKET, 0L);
                String selection = null;
                String[] selectionArgs = null;

                //if(bucketId != 0) {
                //    selection = MediaStore.Video.Media.DATA + " not like ? and " + MediaStore.Video.Media.BUCKET_ID + " =? " ;
                //    selectionArgs = new String[] {"%sdcard/glass_cached_files%", Long.toString(bucketId) };
                //} else {
                //selection = MediaStore.Video.Media.DATA + " not like ? ";
                //selectionArgs = new String[] { "%sdcard/glass_cached_files%" };
                //}

                return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        proj, selection, selectionArgs, MediaStore.Audio.Media.DISPLAY_NAME);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished:" + data.getCount());
        mAudioCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static AudioIndexActivity getInstance(){
        return instance;
    }
}
