package com.applocker.app.Database;

import static org.litepal.crud.DataSupport.where;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.Model.FaviterInfo;
import com.applocker.app.Utils.DataUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommLockInfoManager {

    private PackageManager mPackageManager;
    private Context mContext;


    @NonNull
    private Comparator commLockInfoComparator = new Comparator() {

        @Override
        public int compare(Object lhs, Object rhs) {


            CommLockInfo leftCommLockInfo = (CommLockInfo) lhs;
            CommLockInfo rightCommLockInfo = (CommLockInfo) rhs;

            if (leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return 1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            }
            return 0;
        }
    };


    public CommLockInfoManager(Context mContext) {
        this.mContext = mContext;
        mPackageManager = mContext.getPackageManager();
    }

    public synchronized List<CommLockInfo> getAllCommLockInfos() {

        List<CommLockInfo> commLockInfos = DataSupport.findAll(CommLockInfo.class);
        Collections.sort(commLockInfos, commLockInfoComparator);

        return commLockInfos;
    }

    public synchronized void deleteCommLockInfoTable(@NonNull List<CommLockInfo> commLockInfos) {
        for (CommLockInfo info : commLockInfos) {
            DataSupport.deleteAll(CommLockInfo.class, "packageName = ?", info.getPackageName());
        }
    }

    public synchronized void instanceCommLockInfoTable(@NonNull List<ResolveInfo> resolveInfos) throws PackageManager.NameNotFoundException {
        List<CommLockInfo> list = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            boolean isfaviterApp = isHasFaviterAppInfo(resolveInfo.activityInfo.packageName);
            CommLockInfo commLockInfo = new CommLockInfo(resolveInfo.activityInfo.packageName, false, isfaviterApp, false);
            ApplicationInfo appInfo = mPackageManager.getApplicationInfo(commLockInfo.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            String appName = mPackageManager.getApplicationLabel(appInfo).toString();

            if (!commLockInfo.getPackageName().equals(AppConfig.APP_PACKAGE_NAME)) {
                if (isfaviterApp) {
                    commLockInfo.setLocked(true);
                } else {
                    commLockInfo.setLocked(false);
                }
                commLockInfo.setAppName(appName);
                commLockInfo.setSetUnLock(false);

                list.add(commLockInfo);
            }
        }
        list = DataUtil.clearRepeatCommLockInfo(list);

        DataSupport.saveAll(list);
    }

    private boolean isHasFaviterAppInfo(String packageName) {
        List<FaviterInfo> infos = DataSupport.where("packageName = ?", packageName).find(FaviterInfo.class);
        return infos.size() > 0;
    }

    public void lockCommApplication(String packageName) {
        updateLockStatus(packageName, true);
    }

    public void unlockCommApplication(String packageName) {
        updateLockStatus(packageName, false);
    }

    public void lockCustomApplication(String packageName, String Pin, int status) {
        updateLockStatusCustom(packageName, true, Pin, status);
    }

    public void unlockCustomApplication(String packageName) {
        updateLockStatusCustom(packageName, false);
    }

    private void updateLockStatus(String packageName, boolean isLock) {
        ContentValues values = new ContentValues();
        values.put("isLocked", isLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    private void updateLockStatusCustom(String packageName, boolean isLock, String Pin, int status) {
        ContentValues values = new ContentValues();
        values.put("isLocked", isLock);
        values.put("Multilock", isLock);

        switch (status) {
            case 1:
                values.put("pinLock", Pin);
                Log.d("Lock", Pin);
                break;
            case 2:
                values.put("patternLock", Pin);
                Log.d("Lock", Pin);
                break;
        }
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    private void updateLockStatusCustom(String packageName, boolean isLock) {
        ContentValues values = new ContentValues();
        values.put("isLocked", isLock);
        values.put("Multilock", isLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    public boolean isSetUnLock(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);
        for (CommLockInfo commLockInfo : lockInfos) {
            if (commLockInfo.isSetUnLock()) {
                return true;
            }
        }
        return false;
    }

    public boolean isMultiLock(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);
        return lockInfos.get(0).isMultilock();
    }

    public String getAppPin(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);

        if (lockInfos.get(0).getPinLock() != null) {
            return lockInfos.get(0).getPinLock();
        } else {
            return lockInfos.get(0).getPatternLock();
        }

    }

    public Boolean ChangeLockPin(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);

        if (lockInfos.get(0).getPinLock() != null) {
            return false;
        } else {
            return true;
        }

    }

    public boolean isLockedPackageName(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);
        for (CommLockInfo commLockInfo : lockInfos) {
            if (commLockInfo.isLocked()) {
                return true;
            }
        }
        return false;
    }

    public List<CommLockInfo> queryBlurryList(String appName) {
        return DataSupport.where("appName like ?", "%" + appName + "%").find(CommLockInfo.class);
    }

    public void setIsUnLockThisApp(String packageName, boolean isSetUnLock) {
        ContentValues values = new ContentValues();
        values.put("isSetUnLock", isSetUnLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

}
