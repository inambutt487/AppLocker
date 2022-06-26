package com.applocker.app.Views.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Receiver.AlarmReceiver;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.AutoStartHelper;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.LoadApps;
import com.applocker.app.Utils.Pin.activity.PinActivity;

public class LoaderActivity extends BaseActivity {

    private TinyDB tinyDB;

    @NonNull
    private Handler adHandler = new Handler();
    @Nullable
    private Runnable runnable;
    private ConstraintLayout loading, start_free_trial;
    private Button continueBtn;
    private TextView privacytext, termstext;

    @Nullable
    private ObjectAnimator animator;


    public LoadApps loadApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_loader);

        initializeBilling(LoaderActivity.this);

        tinyDB = new TinyDB(this);

        start_free_trial = findViewById(R.id.start_free_trial);
        loading = findViewById(R.id.loader);

        continueBtn = findViewById(R.id.continueBtn);
        if (!tinyDB.getBoolean(AppConfig.FIRST_TIME_APP_INSTALL)) {
            continueBtn.setText(R.string.accept_continue);
        }

        privacytext = findViewById(R.id.privacytext);
        privacytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PrivacyActivity.class, null);
            }
        });

        termstext = findViewById(R.id.termstext);
        termstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TermsActivity.class, null);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadConfig();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        runnable = new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                start_free_trial.setVisibility(View.VISIBLE);
            }
        };
        adHandler.postDelayed(runnable, 1500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loading.setVisibility(View.VISIBLE);
        start_free_trial.setVisibility(View.GONE);
        adHandler.removeCallbacks(runnable); //stop handler when activity not visible
    }


    private void loadConfig() {

        loadApps = new LoadApps(LoaderActivity.this);
        loadApps.refreshApps();

        AutoStartHelper.getInstance().getAutoStartPermission(LoaderActivity.this);

        animator = ObjectAnimator.ofFloat(continueBtn, "alpha", 0.5f, 1);
        animator.setDuration(1500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                Intent intent;
                if (!tinyDB.getBoolean(AppConfig.FIRST_TIME_APP_INSTALL)) {
                    intent = new Intent(LoaderActivity.this, ConfigActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {

                    if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
                        intent = new Intent(LoaderActivity.this, PremiumActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    } else {
                        intent = new Intent(LoaderActivity.this, PinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }

                }
                startActivity(intent);
                finish();
            }
        });

        //start lock services if  everything is already  setup
        if (tinyDB.getBoolean(AppConfig.LOCK_STATE) && !isMyServiceRunning(LockService.class) && haveIgnoreBatteryPermission()) {
            BackgroundManager.getInstance().init(LoaderActivity.this).startService(LockService.class);
            BackgroundManager.getInstance().init(LoaderActivity.this).startAlarmManager();

            try {
                Intent alarmIntent = new Intent(LoaderActivity.this, AlarmReceiver.class);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(LoaderActivity.this, 999, alarmIntent, 0);
                int interval = (86400 * 1000) / 4;
                if (manager != null) {
                    manager.cancel(pendingIntent);
                }
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}