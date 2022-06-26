package com.applocker.app.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.BackgroundManager;


public class BootComplete extends BroadcastReceiver {

    public static final String TAG = "BroadcastReceiver";
    private TinyDB tinyDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Works: BootComplete");

        tinyDB = new TinyDB(context);

        if(tinyDB.getBoolean(AppConfig.LOCK_STATE)){
        BackgroundManager.getInstance().init(context).startService(LockService.class);
        BackgroundManager.getInstance().init(context).startAlarmManager();
        tinyDB.putBoolean(AppConfig.LOCK_STATE, true);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = (86400 * 1000) / 4;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }

    }
}
