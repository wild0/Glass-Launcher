package com.jtxdriggers.android.glass.glasslauncher.task;

/**
 * Created by roy on 2015/5/8.
 */
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.jtxdriggers.android.glass.glasslauncher.R;
import com.jtxdriggers.android.glass.glasslauncher.activity.CaptureActivity;
import com.jtxdriggers.android.glass.glasslauncher.manager.CameraConfigurationManager;


import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Sean Owen
 */
final public  class DecodeRunnable implements Runnable, Camera.PreviewCallback {

    private static final String TAG = DecodeRunnable.class.getSimpleName();

    private final CaptureActivity activity;
    private final Camera camera;
    private final int height;
    private final int width;
    private final byte[] previewBuffer;
    private boolean running;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    public DecodeRunnable(CaptureActivity activity, Camera camera) {
        this.activity = activity;
        this.camera = camera;
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        height = previewSize.height;
        width = previewSize.width;
        previewBuffer = new byte[(height * width * 3) / 2];
        running = true;
        handlerInitLatch = new CountDownLatch(1);
    }

    private Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }


    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler();
        handlerInitLatch.countDown();
        Looper.loop();
    }

    public void startScanning() {
        getHandler().obtainMessage(R.id.decode_start).sendToTarget();
        Log.d("glass", "start_scanning");
    }

    public void stop() {
        getHandler().obtainMessage(R.id.quit).sendToTarget();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.d("glass", "onPreviewFrame:"+running);
        if (running) {
            getHandler().obtainMessage(R.id.decode, data).sendToTarget();
            //Log.d("glass", "decode:");
        }
    }

    private final class DecodeHandler extends Handler {

        private final Map<DecodeHintType,Object> hints;

        DecodeHandler() {
            hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.POSSIBLE_FORMATS,
                    Arrays.asList(BarcodeFormat.AZTEC, BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX));
        }

        @Override
        public void handleMessage(Message message) {
            if (!running) {
                return;
            }
            switch (message.what) {
                case R.id.decode_start:
                    camera.setPreviewCallbackWithBuffer(DecodeRunnable.this);
                    camera.addCallbackBuffer(previewBuffer);

                    Log.d("glass", "decode start");

                    break;
                case R.id.decode:
                    decode((byte[]) message.obj);
                    break;
                case R.id.decode_succeeded:
                    final Result result = (Result) message.obj;
                    //activity.setResult(result);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.setResult(result);
                        }
                    });

                    break;
                case R.id.decode_failed:
                    camera.addCallbackBuffer(previewBuffer);
                    break;
                case R.id.quit:
                    running = false;
                    Looper.myLooper().quit();
                    break;
            }
        }

        private void decode(byte[] data) {
            Result rawResult = null;

            int subtendedWidth = width / CameraConfigurationManager.ZOOM;
            int subtendedHeight = height / CameraConfigurationManager.ZOOM;
            int excessWidth = width - subtendedWidth;
            int excessHeight = height - subtendedHeight;

            //long start = System.currentTimeMillis();
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(data,
                            width, height,
                            excessWidth / 2, excessHeight / 2,
                            subtendedWidth, subtendedHeight,
                            false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = new MultiFormatReader().decode(bitmap, hints);
            } catch (ReaderException re) {
                // continue
            }

            //long end = System.currentTimeMillis();
            //Log.i(TAG, "Decode in " + (end - start));
            Handler handler = getHandler();
            Message message;
            if (rawResult == null) {
                message = handler.obtainMessage(R.id.decode_failed);
            } else {
                Log.i(TAG, "Decode succeeded: " + rawResult.getText());
                message = handler.obtainMessage(R.id.decode_succeeded, rawResult);
            }
            message.sendToTarget();
        }

    }

}
