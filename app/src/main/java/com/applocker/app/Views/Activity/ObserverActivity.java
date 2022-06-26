package com.applocker.app.Views.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.Utils;

public class ObserverActivity extends BaseActivity {

    private TinyDB tinyDB;

    private CheckBox switch_detect_spy;
    private boolean spy_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_observer);
        ;
        tinyDB = new TinyDB(ObserverActivity.this);

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


        switch_detect_spy = findViewById(R.id.switch_detect_spy);
        switch_detect_spy.setChecked(tinyDB.getBoolean(AppConfig.SPY));

        switch_detect_spy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!Utils.haveOverlayPermission(ObserverActivity.this) || !Utils.hasCameraPermissionGranted(ObserverActivity.this) || !Utils.hasStoragePermissionGranted(ObserverActivity.this)) {
                        spy_permission = true;
                        switch_detect_spy.setChecked(false);
                        checkPermission();
                    } else {
                        switch_detect_spy.setChecked(true);
                        tinyDB.putBoolean(AppConfig.SPY, true);
                    }
                } else {
                    switch_detect_spy.setChecked(false);
                    tinyDB.putBoolean(AppConfig.SPY, false);
                }

            }
        });

    }


    public void checkPermission() {
        if (!Utils.haveOverlayPermission(ObserverActivity.this) || !Utils.hasCameraPermissionGranted(ObserverActivity.this) || !Utils.hasStoragePermissionGranted(ObserverActivity.this)) {

            if (!Utils.hasCameraPermissionGranted(ObserverActivity.this) || !Utils.hasStoragePermissionGranted(ObserverActivity.this)) {
                Utils.requestSpyPermission(ObserverActivity.this);
            }

            if (!Utils.haveOverlayPermission(ObserverActivity.this) && Utils.hasCameraPermissionGranted(ObserverActivity.this) || Utils.hasStoragePermissionGranted(ObserverActivity.this)) {
                Utils.askForSystemOverlayPermission(ObserverActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (spy_permission) {
            checkPermission();
        }
    }
}