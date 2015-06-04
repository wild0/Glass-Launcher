package com.jtxdriggers.android.glass.glasslauncher.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.jtxdriggers.android.glass.glasslauncher.adapter.WifiAdapter;
import com.jtxdriggers.android.glass.glasslauncher.callback.BluetoothTextWriterCallBack;
import com.jtxdriggers.android.glass.glasslauncher.component.SoundManager;
import com.jtxdriggers.android.glass.glasslauncher.component.VoiceDetection;
import com.jtxdriggers.android.glass.glasslauncher.dialog.BluetoothInputDialog;

import java.util.List;

/**
 * Created by roy on 2015/2/20.
 */
public class SettingWifiActivity extends Activity  implements GestureDetector.BaseListener, GestureDetector.FingerListener, VoiceDetection.VoiceDetectionListener {
    //TextView mainText;
    public static final int CMD_UPDATE_WIFILIST = 1;

    public static final int CMD_SHOW_TOAST = 99;

    String TAG = "glass";
    static SettingWifiActivity instance = null;

    WifiManager wifiManager;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    //StringBuilder sb = new StringBuilder();
    GestureDetector mGestureDetector;

    BluetoothInputDialog wifiKeyInputDialog = null;


    CardScrollView deviceCardView = null;
    WifiAdapter wa = null;

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == CMD_UPDATE_WIFILIST){
                wa.notifyDataSetChanged();
            }
            else if(msg.what == CMD_SHOW_TOAST){
                Bundle data = msg.getData();
                String message = data.getString("message");
                Toast.makeText(instance, message,Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.custom_setting_wifi_layout);

        wifiKeyInputDialog = new BluetoothInputDialog(instance);



        deviceCardView = (CardScrollView)findViewById(R.id.wifi_device_list);
        //listView = (ListView)findViewById(R.id.listView);
        wa = new WifiAdapter(this);
        deviceCardView.setAdapter(wa);



        //mainText = (TextView) findViewById(cw.glasslife.R.id.mainText);
        deviceCardView.setOnItemClickListener(mItemClickListener);
        // Initiate wifi service manager
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        // Check for wifi is disabled
        if (wifiManager.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            //Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
            //        Toast.LENGTH_LONG).show();

            wifiManager.setWifiEnabled(true);
        }

        // wifi scaned value broadcast receiver
        receiverWifi = new WifiReceiver();
        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);

        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();


        deviceCardView.activate();


        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //wifiManager.startScan();
       // mainText.setText("Starting Scan...");
    }
    public void sendMessage(int cmd, Bundle data) {
        Message msg = new Message();
        msg.what = cmd;
        if (data != null) {
            msg.setData(data);
        }
        handler.sendMessage(msg);

    }
    public void connect(){
        int position = deviceCardView.getSelectedItemPosition();
        final ScanResult sr = (ScanResult)wa.getItem(position);

        int security = getSecurity(sr);
        if(security==SECURITY_NONE){
            connectToWifi(sr.SSID);
        }
        else{
            BluetoothTextWriterCallBack callback = new BluetoothTextWriterCallBack(){

                @Override
                public void setText(String password) {
                    wifiKeyInputDialog.hide();
                    connectToWifi(sr.SSID, password);

                }

                @Override
                public void complete() {


                }
            };

            wifiKeyInputDialog.show(callback);
        }


                /*
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"YOUR_SSID\"";
        wc.preSharedKey = "\"YOUR_PASSWORD\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
            */


    }
    public void connectToWifi(String ssid){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = ssid;
        //wc.preSharedKey = "\"YOUR_PASSWORD\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
    }
    public void connectToWifi(String ssid, String securityKey){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = ssid;
        wc.preSharedKey = securityKey;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
    }


    final static int  SECURITY_WEP = 1;
    final static int  SECURITY_PSK = 2;
    final static int  SECURITY_EAP = 3;
    final static int  SECURITY_NONE = 0;


    public static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }


    public void sendMessage(int cmd) {
        sendMessage(cmd, null);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Refresh");
        getMenuInflater().inflate(R.menu.wifi_function, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {
            case R.id.wifi_discover:
                //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
                return true;
            case R.id.wifi_connect:
                //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                //wifiManager.startScan();
                connect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onHotwordDetected() {

    }

    @Override
    public void onPhraseDetected(int index, String phrase) {

    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            //sb = new StringBuilder();
            wifiList = wifiManager.getScanResults();
            Log.d(TAG, "wifi access count:" + wifiList.size());

            wa.setItems(wifiList);
            //wa.notifyDataSetChanged();
            sendMessage(CMD_UPDATE_WIFILIST);

            //for(int i = 0; i < wifiList.size(); i++){

           // }

            /*
            sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

            for(int i = 0; i < wifiList.size(); i++){

                sb.append(new Integer(i+1).toString() + ". ");
                sb.append(wifiList.get(i).SSID);
                sb.append("\n\n");
            }

            mainText.setText(sb);
            */
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



        if(gesture==Gesture.TAP){
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

    public static SettingWifiActivity getInstance(){
        return instance;
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            openOptionsMenu();
        }
    };

    //private List<WifiP2pDevice> deviceList = new ArrayList<WifiP2pDevice>();


}

