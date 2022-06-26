package com.applocker.app.Service.SpyCamera;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.Toast;

import com.applocker.app.R;
import com.applocker.app.Utils.NotificationUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class CameraService extends Service implements
        IFrontCaptureCallback {

    Looper myLooper = Looper.myLooper();
    private static boolean isModeActive = false;

    private static class ActionLocks {
        public AtomicBoolean lockCapture;
        public AtomicBoolean lockSmsSend;
        public AtomicBoolean lockEmailSend;
        public AtomicBoolean lockLocationFind;
        public AtomicBoolean lockDataDelete;

        public ActionLocks() {
            lockCapture = new AtomicBoolean(false);
            lockSmsSend = new AtomicBoolean(false);
            lockEmailSend = new AtomicBoolean(false);
            lockLocationFind = new AtomicBoolean(false);
            lockDataDelete = new AtomicBoolean(false);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotification(this, getString(R.string.app_name), "Capture Spy");
        }

        takeAction(null);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Utils.LogUtil.LogD(Constants.LOG_TAG, "Service Destroyed");
//        actionLocks.reset();
        stopSelf();
    }

    private static ActionLocks actionLocks = null;

    private SharedPreferences preferences;
    private String photoPath = null;
    private static GetBackStateFlags stateFlags = new GetBackStateFlags();
    private static GetBackFeatures features = new GetBackFeatures();

    public CameraService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPhotoCaptured(String filePath) {
        synchronized (stateFlags) {
            Log.d("fuck", "k2");
            Toast.makeText(this, "onphotocaputred", Toast.LENGTH_LONG).show();
            stateFlags.isPhotoCaptured = true;
            addBooleanPreference(Constants.PREFERENCE_IS_PHOTO_CAPTURED,
                    stateFlags.isPhotoCaptured);

            Utils.LogUtil.LogD(Constants.LOG_TAG, "Image saved at - "
                    + filePath);
            photoPath = filePath;
            addStringPreference(Constants.PREFERENCE_PHOTO_PATH, photoPath);
        }

        actionLocks.lockCapture.set(false);
        takeAction(null);
    }

    private void addBooleanPreference(String key, boolean value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    private void addStringPreference(String key, String value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private synchronized void takeAction(Bundle bundle) {
        capturePhoto();
    }

    private void capturePhoto() {
        Log.d("fuck", "k");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.LogUtil.LogD(Constants.LOG_TAG,
                        "Inside captureThread run");

                myLooper.prepare();

                // Check if phone is being used.
                CameraView frontCapture = new CameraView(
                        CameraService.this.getBaseContext());
                frontCapture.capturePhoto(CameraService.this);

                myLooper.loop();
            }
        }).start();
    }


    @Override
    public void onCaptureError(int errorCode) {

    }

}