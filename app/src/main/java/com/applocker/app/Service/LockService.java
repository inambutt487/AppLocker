package com.applocker.app.Service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.CommLockInfoManager;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Receiver.ConnectivityReceiver;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.LoadApps;
import com.applocker.app.Utils.NotificationUtil;
import com.applocker.app.Utils.Pin.activity.CorePinActivity;
import com.applocker.app.Views.Dailog.InstalledActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class LockService extends IntentService {

    public static final String UNLOCK_ACTION = "UNLOCK_ACTION";
    public static final String LOCK_SERVICE_LASTTIME = "LOCK_SERVICE_LASTTIME";
    public static final String LOCK_SERVICE_LASTAPP = "LOCK_SERVICE_LASTAPP";
    private static final String TAG = "LockService";
    public static boolean isActionLock = false;
    public boolean threadIsTerminate = false;

    @Nullable
    public String savePkgName;
    UsageStatsManager sUsageStatsManager;
    Timer timer = new Timer();

    private long lastUnlockTimeSeconds = 0;
    private String lastUnlockPackageName = "";
    private boolean lockState;

    private ServiceReceiver mServiceReceiver;
    private CommLockInfoManager mLockInfoManager;

    @Nullable
    private ActivityManager activityManager;

    public LockService() {
        super("LockService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private TinyDB tinyDB;

    @Override
    public void onCreate() {
        super.onCreate();

        tinyDB = new TinyDB(this);

        lockState = tinyDB.getBoolean(AppConfig.LOCK_STATE);
        mLockInfoManager = new CommLockInfoManager(this);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mServiceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        filter.addAction(UNLOCK_ACTION);


        registerReceiver(mServiceReceiver, filter);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            sUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        }

        threadIsTerminate = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotification(this, getString(R.string.app_name), "Advance App Locker Running");
        }

    }

    public String appName(Context context, String packageName) {

        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            return context.getPackageManager().getApplicationLabel(app).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        runForever();
    }

    private void runForever() {

        while (threadIsTerminate) {
            String packageName = getLauncherTopApp(LockService.this, activityManager);
            /*Log.d("getLauncherTopApp", packageName);*/
            if (lockState && !TextUtils.isEmpty(packageName) && !inWhiteList(packageName)) {

                boolean isLockOffScreenTime = tinyDB.getBoolean(AppConfig.LOCK_AUTO_SCREEN_TIME);
                boolean isLockOffScreen = tinyDB.getBoolean(AppConfig.LOCK_AUTO_SCREEN);
                savePkgName = tinyDB.getString(AppConfig.LOCK_LAST_LOAD_PKG_NAME);

                if (isLockOffScreenTime && !isLockOffScreen) {


                    long time = tinyDB.getLong(AppConfig.LOCK_CURR_MILLISECONDS, 0);
                    long leaverTime = tinyDB.getLong(AppConfig.LOCK_APART_MILLISECONDS, 0);
                    if (!TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName) && !savePkgName.equals(packageName)) {
                        if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                            boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                            if (!isSetUnLock) {
                                if (System.currentTimeMillis() - time > leaverTime) {
                                    mLockInfoManager.lockCommApplication(savePkgName);
                                }
                            }
                        }
                    }
                }

                if (isLockOffScreenTime && isLockOffScreen) {
                    long time = tinyDB.getLong(AppConfig.LOCK_CURR_MILLISECONDS, 0);
                    long leaverTime = tinyDB.getLong(AppConfig.LOCK_APART_MILLISECONDS, 0);
                    if (!TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName) && !savePkgName.equals(packageName)) {
                        if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                            boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                            if (!isSetUnLock) {
                                if (System.currentTimeMillis() - time > leaverTime) {
                                    mLockInfoManager.lockCommApplication(savePkgName);
                                }
                            }
                        }
                    }
                }


                if (!isLockOffScreenTime && isLockOffScreen && !TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName)) {
                    if (!savePkgName.equals(packageName)) {
                        isActionLock = false;
                        if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                            boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                            if (!isSetUnLock) {
                                mLockInfoManager.lockCommApplication(savePkgName);
                            }
                        }
                    } else {
                        isActionLock = true;
                    }
                }
                if (!isLockOffScreenTime && !isLockOffScreen && !TextUtils.isEmpty(savePkgName) && !TextUtils.isEmpty(packageName) && !savePkgName.equals(packageName)) {
                    if (getHomes().contains(packageName) || packageName.contains("launcher")) {
                        boolean isSetUnLock = mLockInfoManager.isSetUnLock(savePkgName);
                        if (!isSetUnLock) {
                            mLockInfoManager.lockCommApplication(savePkgName);
                        }
                    }
                }
                if (mLockInfoManager.isLockedPackageName(packageName)) {
                    passwordLock(packageName);
                    continue;
                }
            }
            try {
                Thread.sleep(210);
            } catch (Exception ignore) {
            }
        }

    }

    private boolean inWhiteList(String packageName) {
        return packageName.equals(AppConfig.APP_PACKAGE_NAME);
    }

    public String getLauncherTopApp(@NonNull Context context, @NonNull ActivityManager activityManager) {
        //isLockTypeAccessibility = SpUtil.getInstance().getBoolean(AppConfig.LOCK_TYPE, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    @NonNull
    private List<String> getHomes() {
        List<String> names = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    private void passwordLock(String packageName) {
        AppLocker.getInstance().clearAllActivity();
        Intent intent = new Intent(this, CorePinActivity.class);

        intent.putExtra(AppConfig.LOCK_PACKAGE_NAME, packageName);
        intent.putExtra(AppConfig.LOCK_FROM, AppConfig.LOCK_FROM_FINISH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadIsTerminate = false;
        timer.cancel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.cancelNotification(this);
        }

        unregisterReceiver(mServiceReceiver);

        if(tinyDB.getBoolean(AppConfig.LOCK_STATE)){
            Intent intent = new Intent(this, ConnectivityReceiver.class);
            intent.putExtra("type", "lockservice");
            sendBroadcast(intent);

            BackgroundManager.getInstance().init(this).startService(LockService.class);
            BackgroundManager.getInstance().init(this).startAlarmManager();
            tinyDB.putBoolean(AppConfig.LOCK_STATE, true);
        }


    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        threadIsTerminate = false;
        timer.cancel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.cancelNotification(this);
        }
        lockState = tinyDB.getBoolean(AppConfig.LOCK_STATE);
        if (lockState) {
            Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
            restartServiceTask.setPackage(getPackageName());
            PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1495, restartServiceTask, PendingIntent.FLAG_MUTABLE);
            AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            myAlarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1500,
                    restartPendingIntent);
        }
    }

    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();

            boolean isLockOffScreen = tinyDB.getBoolean(AppConfig.LOCK_AUTO_SCREEN);
            boolean isLockOffScreenTime = tinyDB.getBoolean(AppConfig.LOCK_AUTO_SCREEN_TIME);

            switch (action) {

                case UNLOCK_ACTION:
                    lastUnlockPackageName = intent.getStringExtra(LOCK_SERVICE_LASTAPP);
                    lastUnlockTimeSeconds = intent.getLongExtra(LOCK_SERVICE_LASTTIME, lastUnlockTimeSeconds);
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    tinyDB.putLong(AppConfig.LOCK_CURR_MILLISECONDS, System.currentTimeMillis());
                    if (!isLockOffScreenTime && isLockOffScreen) {
                        String savePkgName = tinyDB.getString(AppConfig.LOCK_LAST_LOAD_PKG_NAME);
                        if (!TextUtils.isEmpty(savePkgName)) {
                            if (isActionLock) {
                                mLockInfoManager.lockCommApplication(lastUnlockPackageName);
                            }
                        }
                    }
                    break;
            }


            tinyDB = new TinyDB(context);
            mLockInfoManager = new CommLockInfoManager(context);

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) && tinyDB.getBoolean(AppConfig.NEW_APP_INSTALL)) {
                String[] a = intent.getDataString().split(":");
                String packageName = a[a.length - 1];

                Log.d(TAG, "PACKAGE_ADDED: " + packageName);

                if (tinyDB.getBoolean(AppConfig.NEW_APP_INSTALL)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("appName", appName(context, packageName));
                    bundle.putString("packageName", packageName);
                    try {

                        LoadApps loadApps = new LoadApps(context);
                        loadApps.refreshApps();
                        
                        context.startActivity(new Intent(context, InstalledActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtras(bundle));
                    } catch (final Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }


            }

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {

                String[] a = intent.getDataString().split(":");
                String packageName = a[a.length - 1];
                Log.d(TAG, "PACKAGE_REMOVED: " + packageName);

                mLockInfoManager.unlockCommApplication(packageName);
            }


        }
    }
}
