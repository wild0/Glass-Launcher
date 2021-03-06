package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.component.VoiceDetection;
import com.wild0.android.glasslauncher.constant.CommandConstant;
import com.wild0.android.glasslauncher.constant.VoiceConstant;
import com.wild0.android.glasslauncher.manager.VoiceControlManager;

/**
 * Created by roy on 2015/4/24.
 */
public class ApplicationIndexActivity extends Activity implements GestureDetector.BaseListener, GestureDetector.FingerListener, VoiceDetection.VoiceDetectionListener{
    static ApplicationIndexActivity instance = null;
    GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;



        setContentView(R.layout.custom_application_layout);




        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
    }


    public boolean onGesture(Gesture gesture) {
        Log.d("glass", "gesture:" + gesture);



        if(gesture==Gesture.SWIPE_LEFT){
            //Intent videoIntent = new Intent(instance, AudioIndexActivity.class);
            // instance.startActivity(videoIntent);
            Intent profileIntent = new Intent(instance, ProfileIndexActivity.class);
            instance.startActivity(profileIntent);
            //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
            //instance.startActivity(cameraIntent);
        }
        else if(gesture==Gesture.SWIPE_RIGHT){


            Intent homeIntent = new Intent(instance, HomeActivity.class);
            instance.startActivity(homeIntent);
            //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            finish();

            //Intent cameraIntent = new Intent(instance, CameraActivity.class);
            //instance.startActivity(cameraIntent);

            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
            //finish();
        }
        else if(gesture==Gesture.TAP){
            instance.openOptionsMenu();
        }
        else if(gesture==Gesture.SWIPE_DOWN){
            //finish();
        }
        return true;
    }


    public void onFingerCountChanged(int i, int i2) {

    }
    @Override
    protected void onPause() {
        super.onPause();
        VoiceControlManager.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        VoiceControlManager.start(instance);
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
        getMenuInflater().inflate(R.menu.application_index, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {
            case R.id.application_list_enter:
                Intent applicationListIntent = new Intent(instance, ApplicationListActivity.class);
                instance.startActivity(applicationListIntent);
                break;
            default:
                break;
                //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onHotwordDetected() {
        Log.d("glass","howto enable");


        android.media.AudioManager mAudioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAudioManager.playSoundEffect(Sounds.TAP);

        showMenuView();
    }
    int menuEnable = 0 ;

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
    public void showMenuView(){
        menuEnable = 1;
        sendMessage(CommandConstant.CMD_SHOW_VOICE_MENU);
    }
    public void hideMenuView(){
        menuEnable = 0;
        sendMessage(CommandConstant.CMD_HIDE_VOICE_MENU);
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
}
