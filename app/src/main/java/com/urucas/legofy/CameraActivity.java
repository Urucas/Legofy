package com.urucas.legofy;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vruno on 11/18/15.
 */
public class CameraActivity extends ActionBarActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private ArrayList<Integer> cameras = new ArrayList<>();
    private int selectedCamera;
    private FrameLayout cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);

        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for(int i=0;i<cameraCount;i++) {
            cameras.add(i);
        }
        startCameraPreview(cameras.get(0));
    }

    private void startCameraPreview(int cameraId) {
        Log.i("camera id", String.valueOf(cameraId));
        selectedCamera = cameraId;
        mCamera = getCameraInstance(cameraId);
        if(mCamera !=null) {
            mPreview = null;
            mCamera.startPreview();
            mPreview = new CameraPreview(this, mCamera);
            cameraPreview.addView(mPreview);
        }else{
            Log.i("no camera", "no");
        }
    }

    private Camera getCameraInstance(int cameraId){
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            mCamera = Camera.open(cameraId);
            // mCamera.setDisplayOrientation(90);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera.Parameters params = mCamera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mCamera;
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback2 {
        private static final String TAG = "Camera TAG";
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (mHolder.getSurface() == null){
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mCamera.stopPreview();
            mCamera.release();
        }catch(Exception e){}
        finish();
    }
}
