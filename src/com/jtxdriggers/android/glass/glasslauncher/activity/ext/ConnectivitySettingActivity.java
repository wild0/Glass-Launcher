package com.jtxdriggers.android.glass.glasslauncher.activity.ext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.jtxdriggers.android.glass.glasslauncher.activity.HomeActivity;

/**
 * Created by roy on 2015/2/20.
 */
public class ConnectivitySettingActivity extends Activity
        implements GestureDetector.BaseListener, GestureDetector.FingerListener {
    String TAG = "glass";
    ConnectivitySettingActivity instance = null;
    GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;
        Log.d(TAG, "onCreate:start");
        setContentView(R.layout.custom_launcher_index_layout);
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
            //Intent cameraIntent = new Intent(instance, CameraActivity.class);
            //instance.startActivity(cameraIntent);
            //finish();
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent homeIntent = new Intent(instance, HomeActivity.class);
            instance.startActivity(homeIntent);
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
}
