package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.service.LocationService;

/**
 * Created by roy on 2015/2/28.
 */
public class LocationActivity extends Activity
{
    String TAG = "glass";
    // For tap event
    private GestureDetector mGestureDetector;

    // Service to handle liveCard publishing, etc...
    private boolean mIsBound = false;
    private LocationService locationDemoLocalService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected() called.");
            locationDemoLocalService = ((LocationService.LocalBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected() called.");
            locationDemoLocalService = null;
        }
    };
    private void doBindService()
    {
        bindService(new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    private void doUnbindService() {
        if (mIsBound) {
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }
    private void doStartService()
    {
        startService(new Intent(this, LocationService.class));
    }
    private void doStopService()
    {
        stopService(new Intent(this, LocationService.class));
    }


    @Override
    protected void onDestroy()
    {
        doUnbindService();
        // doStopService();   // TBD: When do we call Stop service???
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called.");

        setContentView(R.layout.custom_location_layout);

        // For gesture handling.
        mGestureDetector = createGestureDetector(this);

        // bind does not work. We need to call start() explilicitly...
        // doBindService();
        doStartService();
        // TBD: We need to call doStopService() when user "closes" the app....
        // ...

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume() called.");

    }



    // TBD:
    // Just use context menu instead of gesture ???
    // ...

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector createGestureDetector(Context context)
    {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.d(TAG, "gesture = " + gesture);
                if (gesture == Gesture.TAP) {
                    handleGestureTap();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    handleGestureTwoTap();
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    private void handleGestureTap()
    {
        Log.d(TAG, "handleGestureTap() called.");
        doStopService();
        finish();
    }

    private void handleGestureTwoTap()
    {
        Log.d(TAG, "handleGestureTwoTap() called.");
    }


}