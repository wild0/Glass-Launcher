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
import android.widget.Toast;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.wild0.android.glasslauncher.callback.DownloadCallBack;
import com.wild0.android.glasslauncher.component.VoiceDetection;
import com.wild0.android.glasslauncher.constant.CommandConstant;
import com.wild0.android.glasslauncher.constant.VoiceConstant;
import com.wild0.android.glasslauncher.dialog.BluetoothInputDialog;
import com.wild0.android.glasslauncher.manager.AudioManager;
import com.wild0.android.glasslauncher.manager.VoiceControlManager;
import com.wild0.android.glasslauncher.task.DownloadTask;

import java.io.File;
import java.net.URL;
import java.util.Timer;

/**
 * Created by roy on 2015/4/21.
 */
public class MediaIndexActivity extends Activity implements GestureDetector.BaseListener, GestureDetector.FingerListener,VoiceDetection.VoiceDetectionListener{

    static MediaIndexActivity instance = null;
    GestureDetector mGestureDetector;
    BluetoothInputDialog urlInputDialog = null;
    final static int CMD_HIDE_URL_DIALOG = 99;
    final static int MEDIA_DOWNLOAD_REQUEST_CODE = 101;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        urlInputDialog = new BluetoothInputDialog(instance);

        setContentView(R.layout.custom_media_layout);




        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        Log.d("glass", "gesture:" + gesture);



        if(gesture==Gesture.SWIPE_LEFT){
            //Intent videoIntent = new Intent(instance, AudioIndexActivity.class);
            // instance.startActivity(videoIntent);
            Intent settingIntent = new Intent(instance, SettingActivity.class);
            instance.startActivity(settingIntent);
            //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            finish();
            //instance.startActivity(cameraIntent);
        }
        else if(gesture==Gesture.SWIPE_RIGHT){
            Intent mapIntent = new Intent(instance, MapActivity.class);
            instance.startActivity(mapIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
            finish();
        }
        else if(gesture==Gesture.TAP){
            instance.openOptionsMenu();
        }
        else if(gesture==Gesture.SWIPE_DOWN){
            //finish();
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
        getMenuInflater().inflate(R.menu.media_index, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        if(menuEnable==0) {
            switch (item.getItemId()) {
                case R.id.media_audio_index_item:
                    Intent audioIntent = new Intent(instance, AudioListActivity.class);
                    instance.startActivity(audioIntent);
                    return true;
                case R.id.media_video_index_item:
                    Intent videoIntent = new Intent(instance, VideoListActivity.class);
                    instance.startActivity(videoIntent);
                    //https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0
                    //Intent settingBluetoothIntent = new Intent(instance, SettingBluetoothActivity.class);
                    //instance.startActivity(settingBluetoothIntent);
                    return true;
                case R.id.media_camera_index_item:
                    Intent cameraIntent = new Intent(instance, CameraActivity.class);
                    instance.startActivity(cameraIntent);
                    //https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0
                    //Intent settingBluetoothIntent = new Intent(instance, SettingBluetoothActivity.class);
                    //instance.startActivity(settingBluetoothIntent);
                    return true;
                case R.id.media_gallery_index_item:
                    Intent galleryIntent = new Intent(instance, GalleryListActivity.class);
                    instance.startActivity(galleryIntent);
                    //https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0
                    //Intent settingBluetoothIntent = new Intent(instance, SettingBluetoothActivity.class);
                    //instance.startActivity(settingBluetoothIntent);

                    return true;
                case R.id.download_media_item:
                    //https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0

                    int QR_CODE_MODE = 70;
                    //Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    //intent.setPackage("com.google.zxing.client.android");
                    // startActivityForResult(intent, QR_CODE_MODE);
                    Intent captureIntent = new Intent(instance, CaptureActivity.class);
                    //Intent captureIntent = new Intent(instance, MapActivity.class);
                    instance.startActivityForResult(captureIntent, MEDIA_DOWNLOAD_REQUEST_CODE);


                /*
                BluetoothTextWriterCallBack callback = new BluetoothTextWriterCallBack(){

                    @Override
                    public void setText(String text) {
                        Log.d("glass","MediaIndexActivity:download:"+text);
                        //urlInputDialog.hide();
                        sendMessage(CMD_HIDE_URL_DIALOG);
                        downloadMedia(text);
                    }

                    @Override
                    public void complete() {

                    }
                };
                    urlInputDialog.show(callback);
                   */

                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else{
            return false;
        }
    }
    public void downloadMedia(String u){
        Log.d("glass","MediaIndexActivity:onActivityResult:u:"+u);
        try {
            //URL url = new URL("https://www.dropbox.com/s/cl8pgjcrkf0kvhf/It%27s%20just%20love.mp3?dl=0");
            URL url = new URL(u);
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
                    String message = "Download failed";
                    Bundle data = new Bundle();
                    data.putString("message",message);
                    sendMessage(CommandConstant.CMD_SHOW_TOAST, data);
                }

                @Override
                public void updateProgress() {

                }
            };
            Log.d("glass","MediaIndexActivity:downloadurl:"+u.toString());
            Timer t = new Timer();
            DownloadTask task = new DownloadTask(url, callback);
            t.schedule(task, 0);


        } catch (Exception e) {
            e.printStackTrace();
            String message = "Download failed";
            Bundle data = new Bundle();
            data.putString("message",message);
            sendMessage(CommandConstant.CMD_SHOW_TOAST, data);
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("glass","MediaIndexActivity:handleMessage:,arg1:"+msg.what);
            switch (msg.what) {

                case CMD_HIDE_URL_DIALOG:

                    urlInputDialog.hide();

                    break;
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
                case CommandConstant.CMD_SHOW_TOAST: {
                    Bundle data = msg.getData();
                    String message = data.getString("message");
                    Toast.makeText(instance, message, Toast.LENGTH_LONG).show();
                }
                //if (null != activity) {
                //    Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                //            Toast.LENGTH_SHORT).show();
                //}
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

    @Override
    public void onHotwordDetected() {
        Log.d("glass","howto enable");


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("glass","MediaIndexActivity:onActivityResult:,arg1:"+requestCode+",arg2:"+resultCode);
        if(requestCode==MEDIA_DOWNLOAD_REQUEST_CODE){
            //回傳下載結果
            if(resultCode==Activity.RESULT_OK){

                String url = data.getStringExtra("SCAN_RESULT");
                Log.d("glass","MediaIndexActivity:onActivityResult:,arg1:"+requestCode+",url:"+url);
                downloadMedia(url);
            }
        }
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

