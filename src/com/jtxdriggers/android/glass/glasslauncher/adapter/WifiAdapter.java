package com.jtxdriggers.android.glass.glasslauncher.adapter;

import android.content.Context;

import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.jtxdriggers.android.glass.glasslauncher.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by roy on 2015/2/20.
 */
public class WifiAdapter extends CardScrollAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    ArrayList<ScanResult> nodes = new ArrayList<ScanResult>();

    public WifiAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;

    }
    public void clear(){
        nodes.clear();
    }

    public void setItems(List<ScanResult> extNodes){
        nodes.clear();

        for(int i=0;i<extNodes.size();i++){
        //    WifiP2pDevice node = extNodes.
            ScanResult result = extNodes.get(i);
            nodes.add(result);


        }
    }


    @Override
    public int getCount() {
        return nodes.size();
    }

    @Override
    public Object getItem(int position) {
        ScanResult node = nodes.get(position);
        return node;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.wifi_device_row, parent, false);
        } else {
            view = convertView;
        }
        ScanResult node = nodes.get(position);
        String deviceName = node.SSID;

        TextView displayNameTV = (TextView)view.findViewById(R.id.wifi_display_name_textView);
        displayNameTV.setText(deviceName);

        return view;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }


}