package com.wild0.android.glasslauncher.view;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by roy on 2015/2/17.
 */
public class CameraView  extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder surfaceHolder = null;
        private Camera camera = null;

        @SuppressWarnings("deprecation")
        public CameraView(Context context)
        {
            super(context);
            surfaceHolder = this.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {



            // Show the Camera display
            try
            {
                camera = Camera.open();

                // Set the Hotfix for Google Glass
                this.setCameraParameters(camera);
                camera.setPreviewDisplay(holder);
            }
            catch (Exception e)
            {
                this.releaseCamera();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            // Start the preview for surfaceChanged
            try {
                if (camera != null) {
                    camera.startPreview();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // Do not hold the camera during surfaceDestroyed - view should be gone
            try {
                this.releaseCamera();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    public void setCameraParameters(Camera camera)
    {
        try {
            if (camera != null) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewFpsRange(30000, 30000);
                camera.setParameters(parameters);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void releaseCamera()
    {
        try {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}