package com.jtxdriggers.android.glass.glasslauncher.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.jtxdriggers.android.glass.glasslauncher.R;

import java.util.ArrayList;

/**
 * Created by roy on 2015/4/1.
 */
public class BtDeviceAdapter extends CardScrollAdapter {

    ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    Context ctx = null;

    private LayoutInflater mInflater;
    public BtDeviceAdapter(Context ctx){
        mInflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }
    public void add(BluetoothDevice device){
        devices.add(device);
    }


    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.bt_device_row, parent, false);
        } else {
            view = convertView;
        }
        BluetoothDevice node = devices.get(position);

        TextView displayNameTV = (TextView)view.findViewById(R.id.bt_display_name_textView);
        displayNameTV.setText(node.getName());

        return view;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }
}
