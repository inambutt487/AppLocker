package com.applocker.app.Utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.Pin.activity.CorePinActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class AppLocker extends Application implements LifecycleEventObserver {

    private static AppLocker application;
    private static List<BaseActivity> activityList;

    public static AppLocker getInstance() {
        return application;
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    private TinyDB tinyDB;


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;


        LitePal.initialize(this);
        tinyDB = new TinyDB(this);

        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        activityList = new ArrayList<>();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel() {

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);
        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW));

    }

    public void doForCreate(BaseActivity activity) {
        activityList.add(activity);
    }

    public void doForFinish(BaseActivity activity) {
        activityList.remove(activity);
    }

    public void clearAllActivity() {
        try {
            for (BaseActivity activity : activityList) {
                if (activity != null && !clearAllWhiteList(activity))
                    activity.clear();
            }
            activityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean clearAllWhiteList(BaseActivity activity) {
        return activity instanceof CorePinActivity;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_STOP) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    if (tinyDB.getBoolean(AppConfig.LOCK_STATE)) {
                        BackgroundManager.getInstance().init(getApplicationContext()).startService(LockService.class);
                        BackgroundManager.getInstance().init(getApplicationContext()).startAlarmManager();
                    }
                }
            });
        }
    }
}
