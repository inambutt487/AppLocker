package com.applocker.app.Interface;

import android.content.Context;

import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.MVP.LockMainPresenter;

import java.util.List;

public interface LockMainContract {


    interface View extends BaseView<Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, LockMainPresenter.ISearchResultListener listener);

        void onDestroy();
    }


}
