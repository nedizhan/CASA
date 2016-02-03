package com.mepsan.callcenter.casa;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaResimleri;

import java.io.IOException;

public class Camera_capture extends Activity implements SurfaceHolder.Callback {

private Camera mCamera;
private SurfaceView surfaceView;
private SurfaceHolder surfaceHolder;
private ImageButton capture_image,capture_ok,capture_iptal;
    String tut="";
    EditText cevap;
    Boolean resim_cekti=false;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.camera_layout);

    SurfaceView cam=(SurfaceView) findViewById(R.id.surfaceview);
    capture_image = (ImageButton) findViewById(R.id.capture_image);
    capture_ok = (ImageButton) findViewById(R.id.capture_ok);
    capture_iptal = (ImageButton) findViewById(R.id.capture_back);
    cevap=(EditText) findViewById(R.id.editText7);

    cam.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    });
    capture_ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    });

    capture_image.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            capture_image.setVisibility(View.GONE);
            capture();
        }
    });

    capture_iptal.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!resim_cekti) {//resim cekilmedi ise kapat
                finish();
                FragmentArizaResimleri.resim=null;
            }
            else
                resim_cekti=false;

                capture_ok.setVisibility(View.GONE);
                capture_image.setVisibility(View.VISIBLE);
                mCamera.startPreview();

        }
    });



    surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
    surfaceHolder = surfaceView.getHolder();
    surfaceHolder.addCallback(Camera_capture.this);
    surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    try {
        mCamera = Camera.open();

        Camera.Parameters p = mCamera.getParameters();
        p.set("rotation", 90);
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(p);

        setCameraDisplayOrientation(mCamera);
        mCamera.setPreviewDisplay(surfaceHolder);
        mCamera.startPreview();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void capture() {
        capture_ok.setVisibility(View.VISIBLE);

            mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    resim_cekti=true;
                    FragmentArizaResimleri.resim = data;
                   camera.stopPreview();

                }
            });
}

    public void setCameraDisplayOrientation(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        Camera.CameraInfo camInfo =
                new Camera.CameraInfo();
        Camera.getCameraInfo(getBackFacingCameraId(), camInfo);


        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (camInfo.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

@Override
public void surfaceChanged(SurfaceHolder holder, int format, int width,
        int height) {

    try {
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

@Override
public void surfaceCreated(SurfaceHolder holder) {
    
    Log.e("Surface Created", "");
    try {
        mCamera.setPreviewDisplay(holder);
    }
    catch (IOException e){}
}

@Override
public void surfaceDestroyed(SurfaceHolder holder) {
    holder.removeCallback(this);
    Log.e("Surface Destroyed", "");
}

@Override
protected void onPause() {
    super.onPause();
    if (mCamera != null) {
        mCamera.stopPreview();
        mCamera.release();
    }
}
}