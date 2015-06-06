package com.wild0.android.glasslauncher.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.wild0.android.glasslauncher.adapter.BtDeviceAdapter;
import com.wild0.android.glasslauncher.component.VoiceDetection;
import com.wild0.android.glasslauncher.constant.BluetoothConstant;
import com.wild0.android.glasslauncher.manager.BluetoothManager;
import com.wild0.android.glasslauncher.manager.VoiceControlManager;
import com.wild0.android.glasslauncher.service.BluetoothCommService;


import java.util.Set;

/**
 * Created by roy on 2015/4/1.
 */
public class SettingBluetoothActivity extends Activity implements GestureDetector.BaseListener, GestureDetector.FingerListener, VoiceDetection.VoiceDetectionListener{

    static SettingBluetoothActivity instance = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final int CMD_UPDATE_BTLIST = 10;
    public static final int CMD_SHOW_TOAST = 99;
    //private static final int CMD_CONNECT_DEVICE = 11;

    BluetoothAdapter mBluetoothAdapter = null;
    GestureDetector mGestureDetector;

    //CardScrollView deviceCardView = null;
    BtDeviceAdapter adapter = null;
    //BluetoothCommService mCommService = null;
    CardScrollView devicedListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        instance = this;
        setContentView(R.layout.custom_setting_bluetooth_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        Log.d("onCreate", "Got default BT adapter and registered receiver.");

        mBluetoothAdapter.startDiscovery();
        Log.d("onCreate", "Started BT discovery...");



        mCardScrollView = new CardScrollView(this);
        mCardScrollView.activate();
        mCardScrollView.setOnItemClickListener(this);
        setContentView(mCardScrollView);
        */






        setContentView(R.layout.custom_setting_bluetooth_layout);

        instance = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        adapter = new BtDeviceAdapter(instance.getApplicationContext());

        // Find and set up the ListView for paired devices
        devicedListView = (CardScrollView) findViewById(R.id.paired_devices);
        devicedListView.setAdapter(adapter);
        devicedListView.activate();
        //devicedListView.setOnItemClickListener(mDeviceClickListener);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


        Log.d("glass","bt device:"+pairedDevices.size());
        if (pairedDevices.size() > 0) {
            //findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device);
            }
        } else {
            //String noDevices = getResources().getText(R.string.none_paired).toString();
            //pairedDevicesArrayAdapter.add(noDevices);
        }
        sendMessage(CMD_UPDATE_BTLIST);

        Log.d("onCreate", "Got default BT adapter and registered receiver.");
        //devicedListView.setOnItemClickListener(this);
        //mBluetoothAdapter.startDiscovery();
        doDiscovery();

        // If there are paired devices, add each one to the ArrayAdapter
        /*
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
        */
        mGestureDetector = new GestureDetector(this).setBaseListener(this).setFingerListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }else {
            //if (mCommService == null) setupComm();
            setupComm();
        }
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        /*
        if(mCommService!=null){
            mCommService.stop();
        }
        */
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupComm() {
        //Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        /*
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });
        */

        // Initialize the BluetoothChatService to perform bluetooth connections
        //mCommService = new BluetoothCommService(instance, mHandler);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
    }



    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void sendMessage(int cmd, Bundle data) {
        Message msg = new Message();
        msg.what = cmd;
        if (data != null) {
            msg.setData(data);
        }
        mHandler.sendMessage(msg);

    }
    /*
    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice", "Pairing BT device " + device.getName() + "...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("pairDevice", "Exception thrown", e);
            return;
        }
        Log.d("pairDevice", "Device " + device.getName() + " paired.");
    }
    */

    public void sendMessage(int cmd) {
        sendMessage(cmd, null);

    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //FragmentActivity activity = getActivity();
            switch (msg.what) {
                case BluetoothConstant.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {

                        case BluetoothCommService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothCommService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothCommService.STATE_LISTEN:
                        case BluetoothCommService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;

                        default:
                            break;
                    }
                    break;
                case BluetoothConstant.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case BluetoothConstant.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case BluetoothConstant.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    //if (null != activity) {
                    //    Toast.makeText(activity, "Connected to "
                     //           + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //}
                    break;
                case BluetoothConstant.MESSAGE_TOAST: {
                    Bundle data = msg.getData();
                    String message = data.getString(BluetoothConstant.TOAST);
                    Toast.makeText(instance, message, Toast.LENGTH_LONG).show();
                }
                    break;
                case CMD_SHOW_TOAST: {
                    Bundle data = msg.getData();
                    String message = data.getString("message");
                    Toast.makeText(instance, message, Toast.LENGTH_LONG).show();
                }
                    //if (null != activity) {
                    //    Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                    //            Toast.LENGTH_SHORT).show();
                    //}
                    break;
                case  CMD_UPDATE_BTLIST:
                    adapter.notifyDataSetChanged();
                    break;

                /*
                case CMD_CONNECT_DEVICE:

                    Bundle data = msg.getData();

                    int position = data.getInt("device_position");
                    BluetoothDevice device = (BluetoothDevice)adapter.getItem(position);
                    pairDevice(device);
                    */

                    //break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                   // Log.d(TAG, "BT not enabled");
                    //Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                    //        Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
        }
    }
    private void doDiscovery() {
        Log.d("glass", "doDiscovery()");

        // Indicate scanning in the title
        //setProgressBarIndeterminateVisibility(true);
        //setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if(mBluetoothAdapter!=null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

            // Request discover from BluetoothAdapter
            mBluetoothAdapter.startDiscovery();
        }
    }


    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        //String address = data.getExtras()
        //        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        //mCommService.connect(device, secure);
    }




    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d("glass","BroadcastReceiver bt device:"+action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                Log.d("glass","BroadcastReceiver bt device status:"+device.getBondState()+":"+BluetoothDevice.BOND_BONDED);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    adapter.add(device);
                    sendMessage(CMD_UPDATE_BTLIST);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //setProgressBarIndeterminateVisibility(false);
                //setTitle(R.string.select_device);
                //if (mNewDevicesArrayAdapter.getCount() == 0) {
                //    String noDevices = getResources().getText(R.string.none_found).toString();
                //    mNewDevicesArrayAdapter.add(noDevices);
                //}
            }
        }
    };

    public void connect(boolean secure){
        int position = devicedListView.getSelectedItemPosition();
        BluetoothDevice device = (BluetoothDevice) adapter.getItem(position);
        BluetoothManager.getBluetoothService().connect(device, secure);
        //mCommService.connect(device, secure);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(0, 0, 0, "Refresh");
        getMenuInflater().inflate(R.menu.bt_function, menu);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        //mainWifi.startScan();
        //mainText.setText("Starting Scan");
        switch(item.getItemId()) {
            case R.id.bt_discover:
                //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                //wifiManager.startScan();
                return true;
            case R.id.bt_secure_connect:
                //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                //wifiManager.startScan();
                connect(true);
                return true;
            case R.id.bt_insecure_connect:
                //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                //wifiManager.startScan();
                connect(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("glass", "connect to device");
        Bundle data = new Bundle();
        data.putInt("device_position", position);
        sendMessage(CMD_CONNECT_DEVICE, data);

    }
    */



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
        Log.d("glass", "gesture:"+gesture);



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

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            openOptionsMenu();
        }
    };

    @Override
    public void onHotwordDetected() {

    }

    @Override
    public void onPhraseDetected(int index, String phrase) {

    }
}
