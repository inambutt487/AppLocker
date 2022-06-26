package com.applocker.app.Views.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Interface.LockMainContract;
import com.applocker.app.MVP.LockMainPresenter;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Views.Fragments.AppsFragment;
import com.applocker.app.Views.Fragments.CustomFragment;
import com.applocker.app.Views.Fragments.LockedFragment;
import com.applocker.app.Views.Fragments.UnLockedFragment;

import java.util.List;


public class SetAppsActivity extends BaseActivity implements LockMainContract.View, View.OnClickListener {


    public static final String TAG = "SetAppsActivity";
    private LockMainPresenter mLockMainPresenter;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_locked_apps);

        initializeView();
    }

    private void initializeView() {

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLockMainPresenter = new LockMainPresenter(this, this);
        mLockMainPresenter.loadAppInfo(SetAppsActivity.this);
    }

    @Override
    public void loadAppInfoSuccess(@NonNull List<CommLockInfo> list) {

        Bundle bundle = getIntent().getExtras();
        int value = bundle.getInt(AppConfig.SET_APP);

        FragmentTransaction transaction = null;

        switch (value) {
            case 1:
                AppsFragment appsFragment = AppsFragment.newInstance(list);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, appsFragment);
                transaction.commit();
                break;

            case 2:
                CustomFragment customFragment = CustomFragment.newInstance(list);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, customFragment);
                transaction.commit();
                break;

            case 3:
                LockedFragment lockedFragment = LockedFragment.newInstance(list);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, lockedFragment);
                transaction.commit();
                break;

            case 4:
                UnLockedFragment unLockedFragment = UnLockedFragment.newInstance(list);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, unLockedFragment);
                transaction.commit();
                break;
        }


    }


    @Override
    public void onBackPressed() {

        startActivity(SetAppsActivity.class, null);
        finish();
    }

    @Override
    public void onClick(@NonNull View view) {
    }
}