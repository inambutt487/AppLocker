package com.applocker.app.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.Nullable;

import com.applocker.app.BuildConfig;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.CommLockInfoManager;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.Model.FaviterInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadApps {

    private Long time = 0L;
    private PackageManager mPackageManager;
    @Nullable
    private CommLockInfoManager mLockInfoManager;
    private TinyDB tinyDB;

    private Intent intent;

    public LoadApps(Context context) {
        mPackageManager = context.getPackageManager();
        mLockInfoManager = new CommLockInfoManager(context);
        tinyDB = new TinyDB(context);
    }

    public void refreshApps() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // code goes here.
                try {
                    startLoadApps();
                } catch (Exception e) {

                }

            }
        });
        t1.start();
    }

    private void startLoadApps() {

        time = System.currentTimeMillis();
        boolean isInitFaviter = tinyDB.getBoolean(AppConfig.LOCK_IS_INIT_FAVITER);
        boolean isInitDb = tinyDB.getBoolean(AppConfig.LOCK_IS_INIT_DB);

        if (!isInitFaviter) {
            tinyDB.putBoolean(AppConfig.LOCK_IS_INIT_FAVITER, true);
            initFavoriteApps();
        }


        intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, 0);

        if (isInitDb) {
            List<ResolveInfo> appList = new ArrayList<>();
            List<CommLockInfo> dbList = null;

            if (mLockInfoManager != null) {
                dbList = mLockInfoManager.getAllCommLockInfos();
            }

            for (ResolveInfo resolveInfo : resolveInfos) {
                if (!resolveInfo.activityInfo.packageName.equals(BuildConfig.APPLICATION_ID)) {
                    appList.add(resolveInfo);
                }
            }


            if (appList.size() > dbList.size()) {
                List<ResolveInfo> reslist = new ArrayList<>();
                HashMap<String, CommLockInfo> hashMap = new HashMap<>();

                for (CommLockInfo info : dbList) {
                    hashMap.put(info.getPackageName(), info);
                }

                for (ResolveInfo info : appList) {
                    if (!hashMap.containsKey(info.activityInfo.packageName)) {
                        reslist.add(info);
                    }
                }
                try {
                    if (reslist.size() != 0)
                        mLockInfoManager.instanceCommLockInfoTable(reslist);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (appList.size() < dbList.size()) {
                List<CommLockInfo> commlist = new ArrayList<>();
                HashMap<String, ResolveInfo> hashMap = new HashMap<>();
                for (ResolveInfo info : appList) {
                    hashMap.put(info.activityInfo.packageName, info);
                }
                for (CommLockInfo info : dbList) {
                    if (!hashMap.containsKey(info.getPackageName())) {
                        commlist.add(info);
                    }
                }

                if (commlist.size() != 0)
                    mLockInfoManager.deleteCommLockInfoTable(commlist);
            }
        } else {
            tinyDB.putBoolean(AppConfig.LOCK_IS_INIT_DB, true);
            try {
                if (mLockInfoManager != null) {
                    mLockInfoManager.instanceCommLockInfoTable(resolveInfos);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void initFavoriteApps() {

        List<String> packageList = new ArrayList<>();
        List<FaviterInfo> faviterInfos = new ArrayList<>();

        //android
        packageList.add("com.android.gallery3d");
        packageList.add("com.android.mms");
        packageList.add("com.android.contacts");
        packageList.add("com.android.email");
        packageList.add("com.android.vending");


        packageList.add("com.android.settings");
        packageList.add("com.android.dialer");
        packageList.add("com.android.camera");
        packageList.add("com.android.file");
        //......

        //google apps
        packageList.add("com.google.android.apps.photos");
        packageList.add("com.google.android.gm");
        packageList.add("com.google.android.youtube");
        packageList.add("com.google.android.apps.tachyon");//duo

        //social app
        packageList.add("org.thoughtcrime.securesms");//signal
        packageList.add("org.telegram.messenger");
        packageList.add("com.whatsapp");
        packageList.add("com.twitter.android");
        packageList.add("com.facebook.katana");
        packageList.add("com.facebook.orca");

        //etc
        packageList.add("org.fdroid.fdroid");
        packageList.add("org.mozilla.firefox");
        packageList.add("org.schabi.newpipe");
        packageList.add("eu.faircode.email");//fair mail
        packageList.add("com.simplemobile.gallery.pro");// simple gallery

        packageList.add("com.mediatek.filemanager");
        packageList.add("com.sec.android.gallery3d");
        packageList.add("com.sec.android.app.myfiles");

        for (String packageName : packageList) {
            FaviterInfo info = new FaviterInfo();
            info.setPackageName(packageName);
            faviterInfos.add(info);
        }

        DataSupport.deleteAll(FaviterInfo.class);
        DataSupport.saveAll(faviterInfos);
    }

}
