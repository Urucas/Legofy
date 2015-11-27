package com.urucas.legofy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.urucas.legofyLib.Legofy;

import java.io.ByteArrayOutputStream;
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
    private boolean isTakingPicture = false;
    private ImageButton cameraBtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);

        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for(int i=0;i<cameraCount;i++) {
            cameras.add(i);
        }

        cameraBtt = (ImageButton) findViewById(R.id.cameraBtt);
        cameraBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        startCameraPreview(cameras.get(0));
    }

    private void startCameraPreview(int cameraId) {
        selectedCamera = cameraId;
        mCamera = getCameraInstance(cameraId);
        if(mCamera !=null) {
            mPreview = null;
            mCamera.startPreview();
            mPreview = new CameraPreview(this, mCamera);
            cameraPreview.addView(mPreview);
        }else{
            Toast.makeText(CameraActivity.this, R.string.error_getting_camera, Toast.LENGTH_SHORT);
            finish();
        }
    }

    private Camera getCameraInstance(int cameraId){
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            mCamera = Camera.open(cameraId);

            mCamera.setDisplayOrientation(0);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera.Parameters params = mCamera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mCamera;
    }

    private void takePicture() {
        if(isTakingPicture) {
            return;
        }
        Toast.makeText(CameraActivity.this, R.string.processing_picture, Toast.LENGTH_LONG);
        isTakingPicture = true;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                mCamera.stopPreview();

                BitmapFactory.Options opt;

                opt = new BitmapFactory.Options();
                opt.inTempStorage = new byte[16 * 1024];
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPictureSize();

                int height11 = size.height;
                int width11 = size.width;
                float mb = (width11 * height11) / 1024000;

                if (mb > 4f)
                    opt.inSampleSize = 4;
                else if (mb > 3f)
                    opt.inSampleSize = 2;

                Matrix mat = new Matrix();
                // rotate according to camera
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(selectedCamera, cameraInfo);
                /*
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mat.postRotate(90);
                }else{
                    mat.postRotate(-90);
                }
                */

                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 80, stream);

                ImageActivity.sharePath = null;
                ImageActivity.picture = bmp;
                
                Intent intent = new Intent(CameraActivity.this, ImageActivity.class);
                startActivity(intent);

                isTakingPicture = false;
                finish();
            }
        });
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback2 {
        private static final String TAG = "Camera TAG";
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private byte[] cameraFrame;

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
                e.printStackTrace();
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
