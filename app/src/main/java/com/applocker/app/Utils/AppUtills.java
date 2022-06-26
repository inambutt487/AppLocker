package com.applocker.app.Utils;

/**
 * Created by Anuraj R(a4anurajr@gmail.com) on 10/20/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppUtills {
    private static Context mContext;

    public AppUtills(Context context) {
        mContext = context;
    }

    // Get a list of installed app
    public List<String> getInstalledPackages() {
        // Initialize a new Intent which action is main
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        // Set the newly created intent category to launcher
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Set the intent flags
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        );
        // Generate a list of ResolveInfo object based on intent filter
        List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(intent, 0);
        // Initialize a new ArrayList for holding non system package names
        List<String> packageNames = new ArrayList<>();
        // Loop through the ResolveInfo list
        for (ResolveInfo resolveInfo : resolveInfoList) {
            // Get the ActivityInfo from current ResolveInfo
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            // If this is not a system app package
            if (!isSystemPackage(resolveInfo)) {
                // Add the non system package to the list
                packageNames.add(activityInfo.applicationInfo.packageName);
            }
        }
        return packageNames;
    }

    // Custom method to determine an app is system app
    public boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    // Custom method to get application label by package name
    public String getApplicationLabelByPackageName(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        ApplicationInfo applicationInfo;
        String label = "Unknown";
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                label = (String) packageManager.getApplicationLabel(applicationInfo);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return label;
    }

    public Date installTimeFromPackageManager(
            String packageName) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            Field field = PackageInfo.class.getField("firstInstallTime");

            long timestamp = field.getLong(info);
            return new Date(timestamp);
        } catch (PackageManager.NameNotFoundException e) {
            return null; // package not found
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        }
        // field wasn't found
        return null;
    }

    public Date lastUpdateTimeFromPackageManager(
            String packageName) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            Field field = PackageInfo.class.getField("lastUpdateTime");

            long timestamp = field.getLong(info);
            return new Date(timestamp);
        } catch (PackageManager.NameNotFoundException e) {
            return null; // package not found
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        }
        // field wasn't found
        return null;
    }


    public static long getApkSize(String packageName)
            throws PackageManager.NameNotFoundException {
        return new File(mContext.getPackageManager().getApplicationInfo(
                packageName, 0).publicSourceDir).length();
    }


}