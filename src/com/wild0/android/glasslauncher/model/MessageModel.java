package com.wild0.android.glasslauncher.model;

import android.util.Log;


import com.wild0.android.glasslauncher.utility.ByteUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by roy on 2015/4/8.
 */
public class MessageModel {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*
        01-04:timestamp
        05-08:cmd
        09-12:status
        13-16:length
        17-20:
         */
    public int status = 0;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    long timestamp = 0;
    int cmd = 0;
    long length = 0;

    public long getData1Length() {
        return data1Length;
    }

    public void setData1Length(long data1Length) {
        this.data1Length = data1Length;
    }

    public long getData2Length() {
        return data2Length;
    }

    public void setData2Length(long data2Length) {
        this.data2Length = data2Length;
    }

    long data1Length = 0;
    long data2Length = 0;


    public MessageModel(){
        timestamp = System.currentTimeMillis();
    }
    public MessageModel(byte[] data, int offset, long datalength){
        timestamp = System.currentTimeMillis();
        Log.d("btcontrol", "data length:" + data.length);
        for(int i=0;i<length;i++){
            Log.d("btcontrol", "data data[" + i + "]:" + data[i]);
        }
        //ByteArrayOutputStream baos = new ByteArrayOutputStream(data);
        byte[] timestampBytes = new byte[8];
        byte[] cmdBytes = new byte[4];
        byte[] statusBytes = new byte[4];
        byte[] lengthBytes = new byte[8];
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        bais.read(timestampBytes, 0, 8);
        bais.read(cmdBytes, 0, 4);
        bais.read(statusBytes, 0, 4);
        bais.read(lengthBytes, 0, 8);

        timestamp = ByteUtility.bytesToLong(timestampBytes);
        cmd = ByteUtility.bytesToInt(cmdBytes);
        status = ByteUtility.bytesToInt(statusBytes);
        length = ByteUtility.bytesToLong(lengthBytes);



    }


    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("cmd", getCmd());
        jsonObj.put("timestamp", getTimestamp());
        jsonObj.put("length", getLength());
        return jsonObj;
    }
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] timestampBytes = ByteUtility.longToBytes(getTimestamp());
        byte[] cmdBytes = ByteUtility.intToBytes(getCmd());
        byte[] statusBytes = ByteUtility.intToBytes(getStatus());
        byte[] lengthBytes = ByteUtility.longToBytes(getLength());

        baos.write(timestampBytes);
        baos.write(cmdBytes);
        baos.write(statusBytes);
        baos.write(lengthBytes);

        return baos.toByteArray();
    }
    public static MessageModel create(byte[] bytes,int offset, long length){

        MessageModel model = new MessageModel(bytes, offset, length);
        return model;
    }
}
