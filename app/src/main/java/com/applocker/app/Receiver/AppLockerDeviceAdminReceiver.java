package com.applocker.app.Receiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.applocker.app.Views.Activity.SplashActivity;
import com.applocker.app.Views.Dailog.InstalledActivity;

public class AppLockerDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final int CAMERA_REQUEST = 1888;
    private SurfaceView sv;
    private boolean safeToTakePicture = false;
    private int count = 0;


    private void showToast(Context context, CharSequence msg) {
        Log.e("MyDeviceAdminRec...", "::>>>>1 ");
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.e("MyDeviceAdminRec...", "::>>>>2 ");
        showToast(context, "Sample Device Admin: enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.e("MyDeviceAdminRec...", "::>>>>4 ");
        showToast(context, "Sample Device Admin: disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        Log.e("MyDeviceAdminRec...", "::>>>>5 ");
        showToast(context, "Sample Device Admin: pw changed");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        Log.e("MyDeviceAdminRec...", "::>>>>6 ");
        showToast(context, "Sample Device Admin: pw failed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        Log.e("MyDeviceAdminRec...", "::>>>>7 ");
        showToast(context, "Sample Device Admin: pw succeeded");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        context.startActivity(new Intent(context, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        return "App won't work if you disable this setting";

    }
}
