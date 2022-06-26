package com.applocker.app.Utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;

public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener {

    public interface CamCallback extends Camera.PreviewCallback {
        void onPreviewFrame(byte[] data, Camera camera);
    }

    private Camera mCamera;
    private CamCallback callback;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, Camera camera, CamCallback callback) {
        super(context);
        mCamera = camera;
        this.callback = callback;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();

        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException t) {
            t.printStackTrace();
        }

        Camera.Size size = getBiggestPictureSize();
        setLayoutParams(new FrameLayout.LayoutParams(size.width, size.height, Gravity.CENTER));
        params.setPreviewSize(size.width, size.height);
        mCamera.setParameters(params);
        mCamera.setPreviewCallback(callback);
        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        setVisibility(INVISIBLE); // Make the surface invisible as soon as it is created
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Put code here to handle texture size change if you want to

        try {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(callback);
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Update your view here!
    }

    private Camera.Size getBiggestPictureSize() {
        Camera.Size result = null;

        for (Camera.Size size : mCamera.getParameters().getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }
}