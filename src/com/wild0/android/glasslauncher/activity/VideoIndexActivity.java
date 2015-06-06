package com.wild0.android.glasslauncher.activity;

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

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;

/**
 * Created by roy on 2015/2/20.
 */
public class VideoIndexActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor>,GestureDetector.BaseListener, GestureDetector.FingerListener {
    String TAG = "glass";
    static VideoIndexActivity instance = null;
    GestureDetector mGestureDetector;

    public static final String EXTRA_MOVIE_BUCKET = "movie bucket";
    public static final int RESULT_VIDEO = 1;
    private static final int URL_LOADER = 0;
    private Cursor mMovieCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;
        Log.d(TAG, "onCreate:start");
        setContentView(R.layout.custom_video_index_layout);
        Log.d(TAG,"onCreate:complete");
        //mGestureDetector = createGestureDetector(instance);
        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
        //mGestureDetector = new GestureDetector(this)
        //        .setOneFingerScrollListener(this).setTwoFingerScrollListener(this);

        Log.d(TAG,"onCreate:mGestureDetector:"+mGestureDetector.toString());

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
        Log.d(TAG, "gesture:"+gesture);



        if(gesture==Gesture.SWIPE_LEFT){
            Intent audioIntent = new Intent(instance, AudioIndexActivity.class);
            instance.startActivity(audioIntent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();

        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            //Intent videoIntent = new Intent(instance, VideoListActivity.class);
            //instance.startActivity(videoIntent);
            Intent homeIntent = new Intent(instance, HomeActivity.class);
            instance.startActivity(homeIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
            finish();
        }else if(gesture==Gesture.TAP){
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

    @Override
    public void onFingerCountChanged(int previousCount, int currentCount) {
        //mFingerCount.setText(Integer.toString(currentCount));
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Refresh");
        getMenuInflater().inflate(R.menu.video_index, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {
            case R.id.enter_video:
                Intent videoListIntent = new Intent(instance, VideoListActivity.class);
                instance.startActivity(videoListIntent);
                return true;
            case R.id.enter_gallery:
                Intent galleryListIntent = new Intent(instance, GalleryListActivity.class);
                instance.startActivity(galleryListIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch(loaderId) {
            case URL_LOADER:
                String[] proj = { MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.ALBUM,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.SIZE };

                long bucketId = getIntent().getLongExtra(EXTRA_MOVIE_BUCKET, 0L);
                String selection = null;
                String[] selectionArgs = null;

                if(bucketId != 0) {
                    selection = MediaStore.Video.Media.DATA + " not like ? and " + MediaStore.Video.Media.BUCKET_ID + " =? " ;
                    selectionArgs = new String[] {"%sdcard/glass_cached_files%", Long.toString(bucketId) };
                } else {
                    selection = MediaStore.Video.Media.DATA + " not like ? ";
                    selectionArgs = new String[] { "%sdcard/glass_cached_files%" };
                }
                Log.d("glass", "load video:"+selection+","+selectionArgs);
                return new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        proj, selection, selectionArgs, MediaStore.Video.Media.DISPLAY_NAME);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static VideoIndexActivity getInstance(){
        return instance;
    }
}