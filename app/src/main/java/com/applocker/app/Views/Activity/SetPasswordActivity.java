package com.applocker.app.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.Interface.PermissionListener;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Views.Fragments.PatternFragment;
import com.applocker.app.Views.Fragments.PinFragment;
import com.applocker.app.Views.Fragments.SettingFragment;

public class SetPasswordActivity extends BaseActivity implements AppConfigListener {

    private TinyDB tinyDB;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_set_password);

        context = SetPasswordActivity.this;
        tinyDB = new TinyDB(context);

        Bundle bundle = getIntent().getExtras();
        int value = bundle.getInt(AppConfig.LOAD_QUESTION);

       switch (value) {
            case 1:
                replaceFragment(new PinFragment(this));
                break;
           case 2:
               replaceFragment(new PatternFragment(this));
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
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSuccess(String Message, int status) {
        onBackPressed();
    }

    @Override
    public void onFailed(String Message, int status) {

    }

    @Override
    public void onAskQuestion(String Message, int status) {

    }

    @Override
    public void onSetPassword(String Message, int status) {

    }

    @Override
    public void onPermission(String Message, int Position, PermissionListener permissionListener) {

    }
}