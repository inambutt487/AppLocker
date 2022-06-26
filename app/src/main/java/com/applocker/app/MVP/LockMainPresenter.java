package com.applocker.app.MVP;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.CommLockInfoManager;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.LockMainContract;
import com.applocker.app.Model.CommLockInfo;

import java.util.Iterator;
import java.util.List;

public class LockMainPresenter implements LockMainContract.Presenter {

    private LockMainContract.View mView;
    private PackageManager mPackageManager;
    private CommLockInfoManager mLockInfoManager;
    private Context mContext;
    private LoadAppInfo mLoadAppInfo;
    private TinyDB tinyDB;

    public LockMainPresenter(LockMainContract.View view, Context mContext) {
        mView = view;
        this.mContext = mContext;
        mPackageManager = mContext.getPackageManager();
        mLockInfoManager = new CommLockInfoManager(mContext);
        tinyDB = new TinyDB(mContext);
    }


    @Override
    public void loadAppInfo(Context context) {
        mLoadAppInfo = new LoadAppInfo();
        mLoadAppInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void searchAppInfo(String search, ISearchResultListener listener) {
        new SearchInfoAsyncTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search);
    }


    @Override
    public void onDestroy() {
        if (mLoadAppInfo != null && mLoadAppInfo.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadAppInfo.cancel(true);
        }
    }

    public interface ISearchResultListener {
        void onSearchResult(List<CommLockInfo> commLockInfos);
    }

    private class LoadAppInfo extends AsyncTask<Void, String, List<CommLockInfo>> {

        @Override
        protected List<CommLockInfo> doInBackground(Void... params) {

            List<CommLockInfo> commLockInfos = mLockInfoManager.getAllCommLockInfos();
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();
            int favoriteNum = 0;

            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove();
                        continue;
                    } else {
                        info.setAppInfo(appInfo);
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            info.setSysApp(true);
                            info.setTopTitle("System Applications");
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle("User Application");
                        }
                    }

                    if (info.isLocked()) {
                        favoriteNum++;
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }

            tinyDB.putInt(AppConfig.LOCK_FAVITER_NUM, favoriteNum);
            return commLockInfos;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mView.loadAppInfoSuccess(commLockInfos);
        }
    }

    private class SearchInfoAsyncTask extends AsyncTask<String, Void, List<CommLockInfo>> {
        private ISearchResultListener mSearchResultListener;

        public SearchInfoAsyncTask(ISearchResultListener searchResultListener) {
            mSearchResultListener = searchResultListener;
        }

        @Override
        protected List<CommLockInfo> doInBackground(String... params) {
            List<CommLockInfo> commLockInfos = mLockInfoManager.queryBlurryList(params[0]);
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();
            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove(); //将有错的app移除
                    } else {
                        info.setAppInfo(appInfo);
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            info.setSysApp(true);
                            info.setTopTitle("System Applications");
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle("User Application");
                        }
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }
            return commLockInfos;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mSearchResultListener.onSearchResult(commLockInfos);
        }
    }
}
