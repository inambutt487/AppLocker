package com.applocker.app.Views.Activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.Interface.PermissionListener;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Views.Fragments.SettingFragment;

public class SettingActivity extends BaseActivity implements AppConfigListener {

    private Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_setting);

        bundle = getIntent().getExtras();
        int value = bundle.getInt(AppConfig.SETTING);

        switch (value) {
            case 1:
                replaceFragment(new SettingFragment(this));
                break;
        }

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    @Override
    public void onSuccess(String Message, int status) {

        //Set Question
        if (status == 2) {
            startActivity(SetQuestionActivity.class, bundle);
        }

        //Set Icons
        if (status == 3) {
            startActivity(IconActivity.class, bundle);
        }

        //Set Observer
        if (status == 4) {
            startActivity(ObserverActivity.class, bundle);
        }

    }

    @Override
    public void onFailed(String Message, int status) {

    }

    @Override
    public void onAskQuestion(String Message, int status) {

        //Change Pin
        bundle.putInt(AppConfig.LOAD_QUESTION, status);
        startActivity(AskQuestionActivity.class, bundle);

    }

    @Override
    public void onSetPassword(String Message, int status) {


    }

    @Override
    public void onPermission(String Message, int Position, PermissionListener permissionListener) {

    }
}