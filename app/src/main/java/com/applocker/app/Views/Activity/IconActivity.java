package com.applocker.app.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.applocker.app.BuildConfig;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;

public class IconActivity extends BaseActivity implements View.OnClickListener {

    private TinyDB tinyDB;
    private Context context;
    private LinearLayout change_icon_camera, change_calender, change_icon_notes, change_icon_weather, change_icon_calculator, change_icon_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_icon);

        context = IconActivity.this;
        tinyDB = new TinyDB(context);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
            RelativeLayout main_setting = findViewById(R.id.main_setting);
            main_setting.setVisibility(View.VISIBLE);
            main_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConfig.IN_APP, true);
                    startActivity(PremiumActivity.class, bundle);
                }
            });
        }

        change_icon_camera = findViewById(R.id.change_icon_camera);
        change_icon_camera.setOnClickListener(this);
        change_calender = findViewById(R.id.change_calender);
        change_calender.setOnClickListener(this);
        change_icon_notes = findViewById(R.id.change_icon_notes);
        change_icon_notes.setOnClickListener(this);
        change_icon_weather = findViewById(R.id.change_icon_weather);
        change_icon_weather.setOnClickListener(this);
        change_icon_calculator = findViewById(R.id.change_icon_calculator);
        change_icon_calculator.setOnClickListener(this);
        change_icon_mail = findViewById(R.id.change_icon_mail);
        change_icon_mail.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_icon_camera) {

            disableAppIcon();
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".one"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, SplashActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        if (v.getId() == R.id.change_calender) {

            disableAppIcon();
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".two"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, SplashActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        if (v.getId() == R.id.change_icon_notes) {

            disableAppIcon();
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".three"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, SplashActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
        if (v.getId() == R.id.change_icon_weather) {

            disableAppIcon();
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".four"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, SplashActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
        if (v.getId() == R.id.change_icon_calculator) {

            disableAppIcon();
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".five"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, SplashActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
        if (v.getId() == R.id.change_icon_mail) {

            disableAppIcon();
            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".six"),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            PackageManager p = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, SplashActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
    }

    public void disableAppIcon() {

        String icon = null;

        for (int i = 1; i <= 6; i++) {

            switch (i) {
                case 1:
                    icon = ".one";
                    break;
                case 2:
                    icon = ".two";
                    break;
                case 3:
                    icon = ".three";
                    break;
                case 4:
                    icon = ".four";
                    break;
                case 5:
                    icon = ".five";
                    break;
                case 6:
                    icon = ".six";
                    break;
            }

            context.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + icon),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

            onBackPressed();
        }
    }
}