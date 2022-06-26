package com.applocker.app.Manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Receiver.AlarmReceiver;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.Utils;

public class ServiceWorker extends Worker {
    private final Context context;
    private String TAG = "MyWorker";
    private TinyDB tinyDB;

    public ServiceWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        tinyDB = new TinyDB(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork called for: " + this.getId());

        if (!Utils.isMyServiceRunning(context, LockService.class) && tinyDB.getBoolean(AppConfig.LOCK_STATE)) {
            Log.d(TAG, "starting service from doWork");


            BackgroundManager.getInstance().init(context).startService(LockService.class);
            BackgroundManager.getInstance().init(context).startAlarmManager();
            tinyDB.putBoolean(AppConfig.LOCK_STATE, true);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = (86400 * 1000) / 4;
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped called for: " + this.getId());
        super.onStopped();
    }
}
