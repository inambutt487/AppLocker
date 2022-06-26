package com.applocker.app.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.BackgroundManager;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class ConnectivityReceiver extends BroadcastReceiver {


    public static final String TAG = "ConnectivityReceiver";
    private TinyDB tinyDB;

    @Override
    public void onReceive(Context context, Intent intent) {

        tinyDB = new TinyDB(context);

        Log.d(TAG, "Works: ConnectivityReceiver");

        String type = intent.getStringExtra("type");
        if (type.contentEquals("lockservice")) {
            BackgroundManager.getInstance().init(context).startService(LockService.class);
        } else if (type.contentEquals("startlockserviceFromAM")) {
            if (!BackgroundManager.getInstance().init(context).isServiceRunning(LockService.class)) {
                BackgroundManager.getInstance().init(context).startService(LockService.class);
            } else {
                BackgroundManager.getInstance().init(context).startAlarmManager();
            }
            tinyDB.putBoolean(AppConfig.LOCK_STATE, true);
        }

    }
}
