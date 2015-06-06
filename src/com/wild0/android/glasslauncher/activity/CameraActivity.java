package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.view.CameraView;

import java.io.File;

/**
 * Created by roy on 2015/2/16.
 */
public class CameraActivity extends Activity
        implements GestureDetector.BaseListener, GestureDetector.FingerListener {
    CameraActivity instance = null;
    CameraView cameraView = null;
    GestureDetector mGestureDetector;
    String TAG = "glass";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initiate CameraView
        cameraView = new CameraView(this);

        // Turn on Gestures
        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);

        setContentView(cameraView);
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
            Intent settingIntent = new Intent(instance, SettingActivity.class);
            instance.startActivity(settingIntent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent musicIntent = new Intent(instance, AudioIndexActivity.class);
            instance.startActivity(musicIntent);
            //overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //instance.startActivity(homeIntent);
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


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                //case CMD_UPDATE_TIME:

                //    break;
            }
        }
    };

    public void sendMessage(int cmd, Bundle data) {
        Message msg = new Message();
        msg.what = cmd;
        if (data != null) {
            msg.setData(data);
        }
        handler.sendMessage(msg);

    }

    public void sendMessage(int cmd) {
        sendMessage(cmd, null);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Refresh");
        getMenuInflater().inflate(R.menu.camera_index, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {
            case R.id.camera_take_picture_index_item:
                takePicture();
                return true;
            case R.id.camera_record_index_item:
                //https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0
                recordVideo();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    private static final int TAKE_PICTURE_REQUEST = 1;
    private static final int CAPTURE_VIDEO_REQUEST_CODE = 2;
    private void recordVideo(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra("android.intent.extra.durationLimit", 60);
        startActivityForResult(intent, CAPTURE_VIDEO_REQUEST_CODE);
    }
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
            // TODO: Show the thumbnail to the user while the full picture is being
            // processed.
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // The picture is ready; process it.
        } else {
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }
}
