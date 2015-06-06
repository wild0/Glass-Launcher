package com.wild0.android.glasslauncher.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.activity.LocationActivity;


import java.util.List;

/**
 * Created by roy on 2015/2/28.
 */
public class LocationService extends Service
{
    // "Life cycle" constants
    // Currently not being used...
    String TAG = "glass";

    // [1] Starts from this..
    private static final int STATE_NORMAL = 1;

    // [2] When panic action has been triggered by the user.
    private static final int STATE_PANIC_TRIGGERED = 2;

    // [3] Note that cancel, or successful send, etc. change the state back to normal
    // These are intermediate states...
    private static final int STATE_CANCEL_REQUESTED = 4;
    private static final int STATE_CANCEL_PROCESSED = 8;
    private static final int STATE_PANIC_PROCESSED = 16;
    // ....

    // Global "state" of the service.
    private int currentState;


    // For live card
    private LiveCard liveCard = null;

    // Location manager
    private LocationManager locationManager = null;
    // Last known location.
    private Location lastLocation = null;
    // When a new location data is available,
    //     we do not always update the data.
    // "processed time": new data was received.
    // "updated time": the location is actually updated based on the new data.
    private long lastProcessedTime = 0L;
    private long lastUpdatedTime = 0L;


    // For periodic data update...
    private final BroadcastReceiver heartBeat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "heartBeat.onReceive()");
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//            wl.acquire();

            // process location
            processLocationData(null);
            // ...

//            wl.release();
        }
    };



    // No need for IPC...
    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();


    @Override
    public void onCreate()
    {
        super.onCreate();
        currentState = STATE_NORMAL;

        initializeLocationManager();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.xxx");   // TBD:..

        if(heartBeat != null) {
            registerReceiver(heartBeat, filter);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "Received start id " + startId + ": " + intent);
        onServiceStart();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // ????
        onServiceStart();
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        // ???
        onServiceStop();
        if(heartBeat != null) {
            unregisterReceiver(heartBeat);
        }
        super.onDestroy();
    }



    // Service state handlers.
    // ....

    private boolean onServiceStart()
    {
        Log.d(TAG, "onServiceStart() called.");

        // Publish live card...
        publishCard(this);
        startHeartBeat(this);

        currentState = STATE_NORMAL;
        return true;
    }

    private boolean onServicePause()
    {
        Log.d(TAG, "onServicePause() called.");
        return true;
    }
    private boolean onServiceResume()
    {
        Log.d(TAG, "onServiceResume() called.");
        return true;
    }

    private boolean onServiceStop()
    {
        Log.d(TAG, "onServiceStop() called.");

        // TBD:
        // Unpublish livecard here
        // .....
        unpublishCard(this);
        // ...

        // Stop the heart beat.
        // ???
        // onServiceStop() is called when the service is destroyed.... ??? Need to check
        if(heartBeat != null) {
            stopHeartBeat(this);
        }
        // ...

        return true;
    }




    // For live cards...

    private void publishCard(Context context)
    {
        publishCard(context, false);
    }
    private void publishCard(Context context, boolean update)
    {
        Log.d(TAG, "publishCard() called: update = " + update);
        if (liveCard == null || update == true) {

//            // TBD:
//            // We get multiple liveCards if we just call setViews() and publish()...
//            // As a workaround, for now, we always unpublish the previous card first.
//            if (liveCard != null) {
//                liveCard.unpublish();
//            }
//            // ....

            final String cardId = "locationdemo_card";
            //TimelineManager tm = TimelineManager.from(context);
            if(liveCard == null) {
                // if(liveCard == null || ! liveCard.isPublished()) {

                liveCard = new LiveCard(context, "MyLiveCardTag");
                liveCard.publish(LiveCard.PublishMode.REVEAL);

                //liveCard = tm.createLiveCard(cardId);
//                 liveCard.setNonSilent(true);       // for testing.
            }
            // TBD: The reference to remoteViews can be kept in this service as well....
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.livecard_locationdemo);
            String content = "";
            if(lastLocation != null) {
                content = "Location:\n";
                content += lastLocation.getLatitude();
                content += ", ";
                content += lastLocation.getLongitude();
            }
            remoteViews.setCharSequence(R.id.livecard_content, "setText", content);
            liveCard.setViews(remoteViews);
            Intent intent = new Intent(context, LocationActivity.class);
            liveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
            // ???
            // Without this if(),
            // I get an exception:
            // "java.lang.IllegalStateException: State CREATED expected, currently in PUBLISHED"
            // Why???
            if(! liveCard.isPublished()) {
                liveCard.publish(LiveCard.PublishMode.REVEAL);
            } else {
                // ????
                // According to the GDK doc,
                // it appears we should call publish() every time the content changes...
                // But, it seems to work without re-publishing...

                    long now = System.currentTimeMillis();
                    Log.d(TAG, "liveCard not published at " + now);

            }
        } else {
            // Card is already published.
            return;
        }
    }

    private void unpublishCard(Context context)
    {
        Log.d(TAG, "unpublishCard() called.");
        if (liveCard != null) {
            liveCard.unpublish();
            liveCard = null;
        }
    }


    // Location methods
    private void initializeLocationManager()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            if (locationManager.isProviderEnabled(provider)) {
                Log.d(TAG, "Location provider added: provider = " + provider);
            } else {
                Log.d(TAG, "Location provider not enabled: " + provider);
            }
            // TBD: Do this only for the currently enabled providers????
            try {
                locationManager.requestLocationUpdates(provider, 5000L, 5.0f, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d(TAG, "locationChanged: location = " + location);
                        processLocationData(location);
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d(TAG, "providerDisabled: provider = " + provider);
                        // TBD
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d(TAG, "providerEnabled: provider = " + provider);
                        // TBD
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d(TAG, "statusChanged: provider = " + provider + "; status = " + status + "; extras = " + extras);
                        // TBD
                    }
                });
            } catch (Exception e) {
                // ignore
                Log.d(TAG, "requestLocationUpdates() failed for provider = " + provider);
            }
        }
    }

    private void processLocationData(Location location)
    {
        long now = System.currentTimeMillis();
        if(location == null) {
            Log.d(TAG, "processLocationData() called with null location");
            // TBD:
            // periodic checking???
            // Update the location/time regardless of location ????
            // ...
        } else if(lastLocation == null) {
            lastLocation = location;
            lastUpdatedTime = now;
        } else {
            float lastAcc = lastLocation.getAccuracy();
            float acc = location.getAccuracy();

            if(acc < lastAcc) {
                // The current data is better than the last one
                lastLocation = location;
                lastUpdatedTime = now;
            } else {
                // We use an interesting logic here.
                // If the new location data is "different" from the last data (within the accuracy of the new data),
                // then update the location. Otherwise, consider the new data as "noise".

                double dLat2 = (location.getLatitude() - lastLocation.getLatitude()) * (location.getLatitude() - lastLocation.getLatitude());
                double dLng2 = (location.getLongitude() - lastLocation.getLongitude()) * (location.getLongitude() - lastLocation.getLongitude());
                double dAlt2 = (location.getAltitude() - lastLocation.getAltitude()) * (location.getAltitude() - lastLocation.getAltitude());
                double distance = Math.sqrt(dLat2 + dLng2 + dAlt2);

                // Note the arbitrary factor.
                // We need to "tune" this value...
                if(distance >= acc * 0.001 ) {
                    lastLocation = location;
                    lastUpdatedTime = now;
                } else {
                    Log.d(TAG, "New data is within the error bar from the last location: distance = " + distance + "; new location accuracy = " + acc);
                }
            }
        }

        // if(lastUpdatedTime > lastProcessedTime) {
        if(lastUpdatedTime == now) {
            // TBD:
            // Store the data point in DB, etc...
        }

        // Note: We update lastProcessedTime last.
        lastProcessedTime = now;

        // Update the UI.
        if(lastUpdatedTime == now) {
            // Update the live card.
            publishCard(this, true);
        } else {
            // Skip...
        }
    }



    // For heartbeat

    public void startHeartBeat(Context context)
    {
        Log.d(TAG, "startHeartBeat() called.");

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LocationService.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 5, pi);   // every 5 mins
    }

    public void stopHeartBeat(Context context)
    {
        Log.d(TAG, "stopHeartBeat() called.");

        Intent intent = new Intent(context, LocationService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



}