package com.applocker.app.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;


import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PackageDetails {

    public static boolean mSystemApp = true;
    public static CharSequence mApplicationName;
    public static Drawable mApplicationIcon;
    public static List<String> mBatchList = new ArrayList<>();
    public static String mApplicationID;
    public static String mDirData;
    public static String mDirNatLib;
    public static String mDirSource;
    public static String mPath;
    public static String mSearchText;

    public static void makePackageFolder() {
        File file = new File(getPackageDir());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static PackageManager getPackageManager(Context context) {
        return context.getApplicationContext().getPackageManager();
    }

    public static PackageInfo getPackageInfo(String packageName, Context context) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static ApplicationInfo getAppInfo(String packageName, Context context) {
        try {
            return getPackageManager(context).getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getAppName(String packageName, Context context) {
        return getPackageManager(context).getApplicationLabel(Objects.requireNonNull(getAppInfo(
                packageName, context))) + (isEnabled(packageName, context) ? "" : " (Disabled)");
    }

    public static Drawable getAppIcon(String packageName, Context context) {
        return getPackageManager(context).getApplicationIcon(Objects.requireNonNull(getAppInfo(packageName, context)));
    }

    public static String getSourceDir(String packageName, Context context) {
        return Objects.requireNonNull(getAppInfo(packageName, context)).sourceDir;
    }

    public static String getParentDir(String packageName, Context context) {
        return Objects.requireNonNull(new File(Objects.requireNonNull(getAppInfo(packageName, context))
                .sourceDir).getParentFile()).toString();
    }

    public static String getNativeLibDir(String packageName, Context context) {
        return Objects.requireNonNull(getAppInfo(packageName, context)).nativeLibraryDir;
    }

    public static String getDataDir(String packageName, Context context) {
        return Objects.requireNonNull(getAppInfo(packageName, context)).dataDir;
    }

    public static String getVersionName(String path, Context context) {
        return Objects.requireNonNull(getPackageManager(context).getPackageArchiveInfo(path, 0)).versionName;
    }

    public static String getInstalledDate(String path, Context context) {
        return DateFormat.getDateTimeInstance().format(Objects.requireNonNull(getPackageInfo(path, context)).firstInstallTime);
    }

    public static String getUpdatedDate(String path, Context context) {
        return DateFormat.getDateTimeInstance().format(Objects.requireNonNull(getPackageInfo(path, context)).lastUpdateTime);
    }


    public static boolean isEnabled(String packageName, Context context) {
        return Objects.requireNonNull(getAppInfo(packageName, context)).enabled;
    }

    public static boolean isSystemApp(String packageName, Context context) {
        return (Objects.requireNonNull(getAppInfo(packageName, context)).flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static CharSequence getAPKName(String apkPath, Context context) {
        PackageInfo pi = getPackageManager(context).getPackageArchiveInfo(apkPath, 0);
        if (pi != null) {
            return pi.applicationInfo.loadLabel(getPackageManager(context));
        } else {
            return null;
        }
    }

    public static String getAPKId(String apkPath, Context context) {
        PackageInfo pi = getPackageManager(context).getPackageArchiveInfo(apkPath, 0);
        if (pi != null) {
            return pi.applicationInfo.packageName;
        } else {
            return null;
        }
    }

    public static Drawable getAPKIcon(String apkPath, Context context) {
        PackageInfo pi = getPackageManager(context).getPackageArchiveInfo(apkPath, 0);
        if (pi != null) {
            return pi.applicationInfo.loadIcon(getPackageManager(context));
        } else {
            return null;
        }
    }

    public static String getPackageDir() {
        return Environment.getExternalStorageDirectory().toString() + "/Package_Manager";
    }

    public static String getAPKSize(String path) {
        long size = new File(path).length() / 1024;
        long decimal = (size - 1024) / 1024;
        if (size > 1024) {
            return size / 1024 + "." + decimal + " MB";
        } else {
            return size + " KB";
        }
    }

    public static String getBatchList() {
        return mBatchList.toString().substring(1, mBatchList.toString().length() - 1);
    }

    public static String showBatchList() {
        String[] array = getBatchList().trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            if (s != null && !s.isEmpty())
                sb.append(" - ").append(s.replaceAll(",", " ")).append("\n");
        }
        return "\n" + sb.toString();
    }

}