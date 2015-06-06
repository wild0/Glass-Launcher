package com.wild0.android.glasslauncher.manager;

/**
 * Created by roy on 2015/5/8.
 */
import android.hardware.Camera;
import android.util.Log;

import com.wild0.android.glasslauncher.utility.CameraConfigurationUtils;


/**
 * @author Sean Owen
 */
final public  class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";

    public static final int ZOOM = 2;

    private CameraConfigurationManager() {
    }

    public static void configure(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(1280, 720);
        //parameters.setPreviewSize(1920, 1080);
        configureAdvanced(parameters);
        camera.setParameters(parameters);
        //logAllParameters(parameters);
    }

    private static void configureAdvanced(Camera.Parameters parameters) {
        CameraConfigurationUtils.setBestPreviewFPS(parameters);
        CameraConfigurationUtils.setBarcodeSceneMode(parameters);
        CameraConfigurationUtils.setVideoStabilization(parameters);
        CameraConfigurationUtils.setMetering(parameters);
        CameraConfigurationUtils.setZoom(parameters, ZOOM);
    }

    private static void logAllParameters(Camera.Parameters parameters) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            for (String line : CameraConfigurationUtils.collectStats(parameters).split("\n")) {
                Log.i(TAG, line);
            }
        }
    }

}
