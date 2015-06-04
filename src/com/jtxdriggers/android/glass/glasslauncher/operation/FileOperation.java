package com.jtxdriggers.android.glass.glasslauncher.operation;

import android.os.Environment;
import android.util.Log;

import com.jtxdriggers.android.glass.glasslauncher.callback.DownloadCallBack;
import com.jtxdriggers.android.glass.glasslauncher.exception.ConnectionFailException;
import com.jtxdriggers.android.glass.glasslauncher.exception.DownloadFailException;

import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by roy on 2015/3/27.
 */
public class FileOperation {

    public static JSONObject downloadMusicOperation(URL url, String downloadName,
                                                   DownloadCallBack callback) throws IOException,

            IllegalStateException, ConnectionFailException, JSONException, DownloadFailException {

        ArrayList<BasicHeader> headers = new ArrayList<BasicHeader>();
        BasicHeader headerContentType = new BasicHeader("Content-Type",
                "application/json");
        headers.add(headerContentType);




        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        // httpConn.setDefaultHostnameVerifier(allHostsValid);

        // httpConn.setUseCaches(false);
        // connection.setFixedLengthStreamingMode((int)size);
        // httpConn.setDoOutput(true); // indicates POST method
        // httpConn.setDoInput(true);
        // httpConn.setChunkedStreamingMode(1024);
        for (int i = 0; i < headers.size(); i++) {
            BasicHeader header = headers.get(i);
            Log.d("glass",
                    "download header:" + header.getName() + ":"
                            + header.getValue());
            httpConn.setRequestProperty(header.getName(), header.getValue());
        }
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestMethod("GET");
        httpConn.connect();

        int status = httpConn.getResponseCode();

        String statusText = httpConn.getResponseMessage();

        Log.d("cosa", "response text(" + status + "):" + statusText);
        Log.d("cosa", "response content-type:" + httpConn.getContentType());
        Log.d("cosa", "response content-length:" + httpConn.getContentLength());
        if (status == org.apache.http.HttpStatus.SC_OK) {
            InputStream is = new BufferedInputStream(httpConn.getInputStream());
            Log.d("cosa", "download input:" + is.available());

            // Log.d("cosa",
            // "download input:"+p.getNode().getFile().getAbsolutePath());
            // FileOutputStream fos = new
            // FileOutputStream(p.getNode().getFile());
            // String fileName =
            // att.getVirtualFile().getCode()+"."+att.getVirtualFile().getExt();
            //String downloadName = AttachmentUtility.getDownloadName(att);

            // File folder =
            // AttachmentActivity.getInstance().getDir("paperless",
            // Context.MODE_PRIVATE);
            // File f = new File(folder, fileName);
            String fileName = downloadName;

            //File meetingFolder = AttachmentUtility
            //        .getMeetingFolder(meetingCode);
            //File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File musicFolder = Environment.getExternalStorageDirectory();
            File f = new File(musicFolder, fileName);

            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }

            Log.d("glass", "download file:" + f.getAbsolutePath());
            // FileOutputStream fos =
            // AttachmentActivity.getInstance().openFileOutput(fileName,
            // Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(f);
            // Log.d("cosa", "download input:"
            // + p.getNode().getFile().getAbsolutePath());
            byte[] buffer = new byte[4096];

            int count = 0;
            long total = 0;
            long fileSize = is.available();

            while ((count = is.read(buffer)) != -1) {
                total += count;
                Log.d("cosa", "download total:" + total);
                fos.write(buffer, 0, count);

                int progress = (int) (total * 100 / fileSize);
                Log.d("cosa", "download progress:" + progress);
                // p.getNode().setDownloadProgress(progress);
                //callback.updateProgress(progress);
            }


            fos.flush();
            fos.close();
            is.close();

            callback.complete(new File(downloadName));
            // if (status == HttpsURLConnection.HTTP_OK) {
			/*
			 * String content = ""; InputStream ris = httpConn.getInputStream();
			 * byte[] data = new byte[RESPONSE_CONTENT_BUFFER]; int n;
			 * ByteArrayBuffer buf = new
			 * ByteArrayBuffer(RESPONSE_CONTENT_BUFFER); while ((n =
			 * ris.read(data)) != -1) { buf.append(data, 0, n); } content = new
			 * String(buf.toByteArray(), HTTP.UTF_8); ris.close();
			 */
            // downloadStatus = STATUS_PAUSE;
            httpConn.disconnect();
            JSONObject responseJSON = new  JSONObject();
            return responseJSON;
        }
        else{
            throw new DownloadFailException();
        }
        // return content;

    }

}
