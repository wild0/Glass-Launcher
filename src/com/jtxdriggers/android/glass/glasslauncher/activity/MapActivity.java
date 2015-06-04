package com.jtxdriggers.android.glass.glasslauncher.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.jtxdriggers.android.glass.glasslauncher.component.VoiceDetection;
import com.jtxdriggers.android.glass.glasslauncher.constant.CommandConstant;
import com.jtxdriggers.android.glass.glasslauncher.constant.VoiceConstant;
import com.jtxdriggers.android.glass.glasslauncher.manager.VoiceControlManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

/**
 * Created by roy on 2015/4/21.
 */
public class MapActivity  extends Activity implements GestureDetector.BaseListener, GestureDetector.FingerListener, VoiceDetection.VoiceDetectionListener{

    static MapActivity instance = null;
    GestureDetector mGestureDetector;



    //private static final String TAG = StaticMapActivity.class.getSimpleName();

    private static final String STATIC_MAP_URL_TEMPLATE =
            "https://maps.googleapis.com/maps/api/staticmap"
                    + "?center=%.5f,%.5f"
                    + "&zoom=%d"
                    + "&sensor=true"
                    + "&size=640x360"
                    + "&scale=1"
                    + "&style=element:geometry%%7Cinvert_lightness:true"
                    + "&style=feature:landscape.natural.terrain%%7Celement:geometry%%7Cvisibility:on"
                    + "&style=feature:landscape%%7Celement:geometry.fill%%7Ccolor:0x303030"
                    + "&style=feature:poi%%7Celement:geometry.fill%%7Ccolor:0x404040"
                    + "&style=feature:poi.park%%7Celement:geometry.fill%%7Ccolor:0x0a330a"
                    + "&style=feature:water%%7Celement:geometry%%7Ccolor:0x00003a"
                    + "&style=feature:transit%%7Celement:geometry%%7Cvisibility:on%%7Ccolor:0x101010"
                    + "&style=feature:road%%7Celement:geometry.stroke%%7Cvisibility:on"
                    + "&style=feature:road.local%%7Celement:geometry.fill%%7Ccolor:0x606060"
                    + "&style=feature:road.arterial%%7Celement:geometry.fill%%7Ccolor:0x888888";

    /** Formats a Google static maps URL for the specified location and zoom level. */
    private static String makeStaticMapsUrl(double latitude, double longitude, int zoom) {
        return String.format(STATIC_MAP_URL_TEMPLATE, latitude, longitude, zoom);
    }

    private ImageView mMapView;

    TextView msgTextView;

    /** Load the map asynchronously and populate the ImageView when it's loaded. */
    private void loadMap(double latitude, double longitude, int zoom) {
        String url = makeStaticMapsUrl(latitude, longitude, zoom);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                try {
                    Log.d("glass", "load image "+urls[0]);
                    HttpResponse response = new DefaultHttpClient().execute(new HttpGet(urls[0]));
                    InputStream is = response.getEntity().getContent();
                    return BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    Log.e("glass", "Failed to load image", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mMapView.setImageBitmap(bitmap);
                    msgTextView.setVisibility(View.GONE);
                }
            }
        }.execute(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VoiceControlManager.start(instance);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VoiceControlManager.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;



        setContentView(R.layout.custom_map_layout);
        msgTextView = (TextView)this.findViewById(R.id.textView9);
        mMapView = (ImageView)this.findViewById(R.id.imageView2);
        //mMapView = new ImageView(this);
        //setContentView(mMapView);
        Log.d("glass", "loadMap");

        loadMap(37.8019, -122.4189, 18);



        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        Log.d("glass", "gesture:" + gesture);



        if(gesture==Gesture.SWIPE_LEFT){
            //Intent videoIntent = new Intent(instance, AudioIndexActivity.class);
            // instance.startActivity(videoIntent);
            Intent mediaIntent = new Intent(instance, MediaIndexActivity.class);
            instance.startActivity(mediaIntent);
            //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
            //instance.startActivity(cameraIntent);
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent profileIntent = new Intent(instance, ProfileIndexActivity.class);
            instance.startActivity(profileIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
            finish();
        }
        else if(gesture==Gesture.TAP){
            instance.openOptionsMenu();
        }
        else if(gesture==Gesture.SWIPE_DOWN){
            finish();
        }
        return true;
    }

    @Override
    public void onFingerCountChanged(int i, int i2) {

    }

    /**
     * Overridden to allow the gesture detector to process motion events that occur anywhere within
     * the activity.
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Refresh");
        getMenuInflater().inflate(R.menu.map_index, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHotwordDetected() {
        android.media.AudioManager mAudioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAudioManager.playSoundEffect(Sounds.TAP);

        showMenuView();
    }
    public int getMenuEnable(){
        return menuEnable;
    }

    @Override
    public void onPhraseDetected(int index, String phrase) {
        if(getMenuEnable()==1) {
            Log.d("glass", "onPhraseDetected :[" + index + "]" + phrase);
            if (phrase.equals("call")) {

            } else if (phrase.equals("take")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, VoiceConstant.TAKE_PICTURE_REQUEST);
            } else if (phrase.equals("record")) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra("android.intent.extra.durationLimit", 20);
                startActivityForResult(intent, VoiceConstant.CAPTURE_VIDEO_REQUEST_CODE);
            } else if (phrase.equals("music")) {
                Intent audioIntent = new Intent(instance, AudioIndexActivity.class);
                instance.startActivity(audioIntent);
            } else if (phrase.equals("video")) {
                Intent videoIntent = new Intent(instance, VideoListActivity.class);
                instance.startActivity(videoIntent);
            } else if (phrase.equals("map")) {
                Intent mapIntent = new Intent(instance, MapActivity.class);
                instance.startActivity(mapIntent);
            } else if (phrase.equals("cancel")) {
                hideMenuView();
                //sendMessage(CMD_HIDE_VOICE_MENU);
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("glass","MediaIndexActivity:handleMessage:,arg1:"+msg.what);
            switch (msg.what) {


                case CommandConstant.CMD_SHOW_VOICE_MENU: {
                    View menu = (View) findViewById(R.id.home_index_menu_layout);
                    menu.setVisibility(View.VISIBLE);
                }
                break;
                case CommandConstant.CMD_HIDE_VOICE_MENU: {
                    View menu = (View) findViewById(R.id.home_index_menu_layout);
                    menu.setVisibility(View.GONE);
                }
                break;

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
    int menuEnable = 0 ;
    public void showMenuView(){
        menuEnable = 1;
        sendMessage(CommandConstant.CMD_SHOW_VOICE_MENU);
    }
    public void hideMenuView(){
        menuEnable = 0;
        sendMessage(CommandConstant.CMD_HIDE_VOICE_MENU);
    }
}

