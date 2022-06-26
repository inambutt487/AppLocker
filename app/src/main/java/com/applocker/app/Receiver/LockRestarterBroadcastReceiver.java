package com.applocker.app.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.Utils;

public class LockRestarterBroadcastReceiver extends BroadcastReceiver {

    private TinyDB tinyDB;
    public static final String TAG = "BroadcastReceiver";
    private boolean lockState = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Works: LockRestarterBroadcastReceiver");

        tinyDB = new TinyDB(context);

        if (intent != null && Utils.isMyServiceRunning(context, LockService.class) && tinyDB.getBoolean(AppConfig.LOCK_STATE)) {
            String type = intent.getStringExtra("type");
            if (type.contentEquals("lockservice")) {
                BackgroundManager.getInstance().init(context).startService(LockService.class);
            } else if (type.contentEquals("startlockserviceFromAM")) {
                if (!BackgroundManager.getInstance().init(context).isServiceRunning(LockService.class)) {
                    BackgroundManager.getInstance().init(context).startService(LockService.class);
                    tinyDB.putBoolean(AppConfig.LOCK_STATE, true);
                }

                //repeat
                BackgroundManager.getInstance().init(context).startAlarmManager();
                tinyDB.putBoolean(AppConfig.LOCK_STATE, true);
            }
        }
    }
}
