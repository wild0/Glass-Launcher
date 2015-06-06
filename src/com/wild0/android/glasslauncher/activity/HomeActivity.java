package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.wild0.android.glasslauncher.GlassLauncherService;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.callback.TickCallBack;
import com.wild0.android.glasslauncher.component.VoiceDetection;
import com.wild0.android.glasslauncher.constant.CommandConstant;
import com.wild0.android.glasslauncher.constant.Constants;
import com.wild0.android.glasslauncher.constant.VoiceConstant;
import com.wild0.android.glasslauncher.manager.BluetoothManager;
import com.wild0.android.glasslauncher.manager.VoiceControlManager;
import com.wild0.android.glasslauncher.service.BluetoothCommService;
import com.wild0.android.glasslauncher.task.ClockTask;
import com.wild0.android.glasslauncher.utility.TimeUtility;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by roy on 2015/2/16.
 */
public class HomeActivity  extends Activity
        implements GestureDetector.BaseListener, GestureDetector.FingerListener, VoiceDetection.VoiceDetectionListener{
    String TAG = "glass";
    static HomeActivity instance = null;
    int menuEnable = 0 ;
    GestureDetector mGestureDetector;

    public final static int CMD_UPDATE_TIME = 10;

    private String voiceAction = null;
    BluetoothCommService mBTService = null;

    //protected VoiceDetection mVoiceDetection;
    //private VoiceDetection.VoiceDetectionListener mListener;
    //private String[] mPhrases = new String[] { "call", "take", "record", "music", "video", "map", "cancel" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;


        if(GlassLauncherService.getInstance()==null) {
            startService(new Intent(this, GlassLauncherService.class));

        }


        Log.d(TAG, "onCreate:start");
        //getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        setContentView(R.layout.custom_launcher_index_layout);



        Log.d(TAG,"onCreate:complete");
        //mGestureDetector = createGestureDetector(instance);
        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);


        //mGestureDetector = new GestureDetector(this)
        //        .setOneFingerScrollListener(this).setTwoFingerScrollListener(this);

        TextView dateTextView = (TextView)findViewById(R.id.date_custom_index_textView);
        TextView timeTextView = (TextView)findViewById(R.id.time_custom_index_textView);


        Log.d(TAG,"onCreate:mGestureDetector:"+mGestureDetector.toString());
        Log.d(TAG, "getIntent = " + getIntent());


        //voiceAction = getVoiceAction(getIntent());
        //if(Log.I) Log.i("voiceAction = " + voiceAction);
        //Log.d(TAG, "voiceAction = " + voiceAction);
        //processVoiceAction(voiceAction);

        //mVoiceMenu = VoiceMenuEss.getInstance(this, VoiceConstant.CMD_ACTIVATE, this, mPhrases);

        //mVoiceDetection = new VoiceDetection(instance, VoiceConstant.CMD_ACTIVATE, instance, false, VoiceConstant.mPhrases);


        //setupBluetooth();
        VoiceControlManager.init(instance);






    }
    ClockTask task = null;
    Timer clockTimer = null;
    public void activeClock(){
        if(clockTimer!=null){
            clockTimer.cancel();
        }
        TickCallBack tickCallBack = new TickCallBack(){

            @Override
            public void change() {
                sendMessage(CMD_UPDATE_TIME);
            }
        };

        task = new ClockTask(tickCallBack);
        clockTimer = new Timer();
        clockTimer.schedule(task, 0);

    }
    public void deactiveClock(){
        if(task!=null){
            task.stop();
        }
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
            //Log.d(TAG, "SWIPE TO video");
            //Intent cameraIntent = new Intent(instance, CameraActivity.class);
            //instance.startActivity(cameraIntent);

            Intent applicationIntent = new Intent(instance, ApplicationIndexActivity.class);
            instance.startActivity(applicationIntent);
            //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent settingIntent = new Intent(instance, SettingActivity.class);
            instance.startActivity(settingIntent);
            //overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
            finish();
        }
        else if(gesture==Gesture.SWIPE_DOWN){
            finish();
        }
        else if(gesture==Gesture.TAP){
            //openVoiceDictationActivity();
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
    protected void onPause() {
        super.onPause();
        VoiceControlManager.stop();
        deactiveClock();
        Log.d("glass", "HomeActivity:onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start();
        VoiceControlManager.start(instance);
        activeClock();
        Log.d("glass", "HomeActivity:onResume");
        //discoverable();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onFingerCountChanged(int previousCount, int currentCount) {
        //mFingerCount.setText(Integer.toString(currentCount));
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //Log.d("glass","HomeActivity:handleMessage:,arg1:"+msg.what);
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
                case CMD_UPDATE_TIME:

                    TextView dateTextView = (TextView)findViewById(R.id.date_custom_index_textView);
                    TextView timeTextView = (TextView)findViewById(R.id.time_custom_index_textView);

                    String timeStr = TimeUtility.convertDateToStr(System.currentTimeMillis(), "HH:mm");
                    String dateStr = TimeUtility.convertDateToStr(System.currentTimeMillis(), "yyyy-MM-dd");

                    dateTextView.setText(dateStr);
                    timeTextView.setText(timeStr);

                    break;
                case Constants.MESSAGE_READ_BT:
                    byte[] readBuf = (byte[]) msg.obj;

                    // construct a string from the valid bytes in the buffer


                    //收到的訊息
                    Log.d("glass","MESSAGE_READ_BT read message:,arg1:"+msg.arg1 +",arg2:"+msg.arg2);
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    BluetoothManager.receiveData(readMessage);
                    Log.d("glass","read message:"+readMessage);
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

    private String getVoiceAction(Intent intent)
    {
        if(intent == null) {
            return null;
        }
        Log.d("glass", "get voice action = " + intent.toString());

        String action = null;
        Bundle extras = intent.getExtras();
        ArrayList<String> voiceActions = null;
        if(extras != null) {
            voiceActions = extras.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
            if(voiceActions != null && !voiceActions.isEmpty()) {
                //if(Log.D) {
                    for(String a : voiceActions) {
                        Log.d(TAG, "action = " + a);
                    }
                //}
                action = voiceActions.get(0);
            }
        }
        return action;
    }

    public void setupBluetooth(){
        mBTService = new BluetoothCommService(instance, mHandler);
        BluetoothManager.setBluetoothService(mBTService);
    }

    // Opens the WordDictation activity,
    // or, quits the program.
    /*
    private void processVoiceAction(String voiceAction)
    {

        Log.d("glass", "process voice action:"+voiceAction);

        if(voiceAction != null) {
            if(voiceAction.equals(VoiceConstant.ACTION_START_DICTATION)) {
                //openVoiceDictationActivity();
            } else if(voiceAction.equals(VoiceConstant.ACTION_STOP_VOICEDEMO)) {
                Log.d(TAG, "VoiceDemo activity has been terminated upon start.");
                this.finish();
            }  else if(voiceAction.equals(VoiceConstant.ACTION_VIDEO)) {
                Log.d(TAG, "Voice activity enter video");
                Intent videoIntent = new Intent(instance, VideoIndexActivity.class);
                instance.startActivity(videoIntent);
                //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
            else {
                Log.d(TAG,"Unknown voice action: " + voiceAction);
            }
        } else {
            Log.d(TAG, "No voice action provided.");
        }
    }
    */

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        Log.d("glass","onCreatePanelMenu:"+featureId);
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.home_index, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        //getMenuInflater().inflate(R.menu.home_index, menu);
       // return true;
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("glass","onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.home_index, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.video_index_item:
                    // handle top-level dogs menu item
                    break;
                case R.id.setting_index_item:
                    Intent settingIntent = new Intent(instance, SettingActivity.class);
                    instance.startActivity(settingIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                default:
                    return true;
            }
            return true;
        }
        // Good practice to pass through to super if not handled
        return super.onMenuItemSelected(featureId, item);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {


            }
        }
    };


    @Override
    public void onHotwordDetected() {
        Log.d("glass","howto enable");


        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAudioManager.playSoundEffect(Sounds.TAP);

        showMenuView();

        //onHotwordDetected();
        //PopupMenu p  = new PopupMenu(instance, null);
        //Menu menu = p.getMenu();

        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.home_index,menu);


        //instance.onCreatePanelMenu(0,menu );
        //instance.onCreatePanelView(1);


    }
    public void showMenuView(){
        menuEnable = 1;
        sendMessage(CommandConstant.CMD_SHOW_VOICE_MENU);
    }
    public void hideMenuView(){
        menuEnable = 0;
        sendMessage(CommandConstant.CMD_HIDE_VOICE_MENU);
    }
    public int getMenuEnable(){
        return menuEnable;
    }

    @Override
    public void onPhraseDetected(int index, String phrase) {
        //if (mVoiceMenu.isVisible())
        //    mVoiceMenu.dismiss();
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
        //onPhraseDetected(index,phrase);
    }
    public void start() {
        Log.d("glass", "mVoiceDetection.start");
        //mVoiceDetection.start();
    }
    /*
    public void discoverable(){
        if (BluetoothAdapter.getDefaultAdapter().getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    */




    public void stop() {

        //mVoiceDetection.stop();
    }


    private class ApplicationsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            //startActivity(new Intent(context, MenuActivity.class));
        }
    }
}
