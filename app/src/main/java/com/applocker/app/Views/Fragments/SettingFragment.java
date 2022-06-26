package com.applocker.app.Views.Fragments;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.R;
import com.applocker.app.Receiver.AppLockerDeviceAdminReceiver;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.LockAutoTime;
import com.applocker.app.Utils.Utils;
import com.applocker.app.Views.Activity.PremiumActivity;
import com.applocker.app.Views.Activity.SettingActivity;
import com.applocker.app.Views.Activity.SpyActivity;
import com.applocker.app.Views.Dailog.SelectLockTimeDialog;


public class SettingFragment extends Fragment implements View.OnClickListener
        , DialogInterface.OnDismissListener {

    public static final String ON_ITEM_CLICK_ACTION = "on_item_click_action";

    public final String TAG = "SettingFragment";
    private AppConfigListener appConfigListener;

    private Context context;

    private ImageView arrow_up1, arrow_up2, arrow_up3;

    @NonNull
    private RelativeLayout setting_layout_security_parent, setting_layout_general_parent, setting_layout_magic_parent;

    @NonNull
    private RelativeLayout setting_layout_security_child, setting_layout_general_child, setting_layout_magic_child;

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    // Interaction with the DevicePolicyManager
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdmin;

    private TextView lock_time;
    private LinearLayout setting_set_security_question, change_lock_pin, change_lock_pattern, setting_lock_time, setting_change_icon, setting_observer, setting_intruder;

    private TinyDB tinyDB;


    private CheckBox enable_lock, immediately_lock, switch_cache, switch_patterns, switch_pattern_visibility,
            switch_fringerprint, switch_vibration, switch_new_lock_app, switch_uninstall;


    private SelectLockTimeDialog dialog;
    private LockSettingReceiver mLockSettingReceiver = null;

    private boolean enable_lock_service;

    @NonNull
    private Bundle bundle;

    public SettingFragment(AppConfigListener appConfigListener) {
        this.appConfigListener = appConfigListener;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLockSettingReceiver = new LockSettingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ON_ITEM_CLICK_ACTION);
        getActivity().registerReceiver(mLockSettingReceiver, filter);

        dialog = new SelectLockTimeDialog(getActivity(), "");
        dialog.setOnDismissListener(this);

        context = getActivity();
        bundle = new Bundle();
        tinyDB = new TinyDB(context);

        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(context, AppLockerDeviceAdminReceiver.class);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        if (getActivity() instanceof SettingActivity) {
            //do something
            ImageView back = view.findViewById(R.id.back);
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
                RelativeLayout main_setting = view.findViewById(R.id.main_setting);
                main_setting.setVisibility(View.VISIBLE);
                main_setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(AppConfig.IN_APP, true);
                        startActivity(new Intent(context, PremiumActivity.class).putExtras(bundle));
                    }
                });
            }
        }

        arrow_up1 = view.findViewById(R.id.arrow_up1);
        arrow_up1.setOnClickListener(this);
        arrow_up2 = view.findViewById(R.id.arrow_up2);
        arrow_up2.setOnClickListener(this);
        arrow_up3 = view.findViewById(R.id.arrow_up3);
        arrow_up3.setOnClickListener(this);

        setting_layout_security_parent = view.findViewById(R.id.setting_layout_security_parent);
        setting_layout_security_parent.setOnClickListener(this);
        setting_layout_general_parent = view.findViewById(R.id.setting_layout_general_parent);
        setting_layout_general_parent.setOnClickListener(this);
        setting_layout_magic_parent = view.findViewById(R.id.setting_layout_magic_parent);
        setting_layout_magic_parent.setOnClickListener(this);

        setting_layout_security_child = view.findViewById(R.id.setting_layout_security_child);
        setting_layout_security_child.setOnClickListener(this);
        setting_layout_general_child = view.findViewById(R.id.setting_layout_general_child);
        setting_layout_general_child.setOnClickListener(this);
        setting_layout_magic_child = view.findViewById(R.id.setting_layout_magic_child);
        setting_layout_magic_child.setOnClickListener(this);

        setting_set_security_question = view.findViewById(R.id.setting_set_security_question);
        setting_set_security_question.setOnClickListener(this);
        change_lock_pin = view.findViewById(R.id.change_lock_pin);
        change_lock_pin.setOnClickListener(this);
        change_lock_pattern = view.findViewById(R.id.change_lock_pattern);
        change_lock_pattern.setOnClickListener(this);
        setting_lock_time = view.findViewById(R.id.setting_lock_time);
        setting_lock_time.setOnClickListener(this);
        setting_change_icon = view.findViewById(R.id.setting_change_icon);
        setting_change_icon.setOnClickListener(this);
        setting_observer = view.findViewById(R.id.setting_observer);
        setting_observer.setOnClickListener(this);
        setting_intruder = view.findViewById(R.id.setting_intruder);
        setting_intruder.setOnClickListener(this);

        lock_time = view.findViewById(R.id.lock_time);
        if (!tinyDB.getString(AppConfig.LOCK_APART_TITLE).isEmpty()) {
            lock_time.setText(tinyDB.getString(AppConfig.LOCK_APART_TITLE));
        }

        enable_lock = view.findViewById(R.id.enable_lock);
        enable_lock.setChecked(Utils.isMyServiceRunning(context, LockService.class));

        enable_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    tinyDB.putBoolean(AppConfig.LOCK_STATE, isChecked);
                    enable_lock.setChecked(isChecked);
                    BackgroundManager.getInstance().init(getActivity()).startService(LockService.class);
                    BackgroundManager.getInstance().init(getActivity()).startAlarmManager();
                } else {
                    tinyDB.putBoolean(AppConfig.LOCK_STATE, false);
                    enable_lock.setChecked(false);
                    BackgroundManager.getInstance().init(getActivity()).stopService(LockService.class);
                    BackgroundManager.getInstance().init(getActivity()).stopAlarmManager();
                }

            }
        });

        switch_pattern_visibility = view.findViewById(R.id.switch_pattern_visibility);
        switch_pattern_visibility.setChecked(tinyDB.getBoolean(AppConfig.PATTERN_VISIBLE));

        switch_pattern_visibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_pattern_visibility.setChecked(isChecked);
                tinyDB.putBoolean(AppConfig.PATTERN_VISIBLE, isChecked);
            }
        });

        switch_fringerprint = view.findViewById(R.id.switch_fringerprint);
        switch_fringerprint.setChecked(tinyDB.getBoolean(AppConfig.FINGER));


        switch_fringerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (tinyDB.getBoolean(AppConfig.FINGER)) {
                    tinyDB.putBoolean(AppConfig.FINGER, isChecked);
                    switch_fringerprint.setChecked(isChecked);
                } else {
                    switch_fringerprint.setChecked(false);
                    Toast.makeText(context, "Please Enable Finger Print Lock", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switch_vibration = view.findViewById(R.id.switch_vibration);
        switch_vibration.setChecked(tinyDB.getBoolean(AppConfig.VIBRATE));

        switch_vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tinyDB.putBoolean(AppConfig.VIBRATE, true);
            }
        });

        switch_uninstall = view.findViewById(R.id.switch_uninstall);
        switch_uninstall.setChecked(tinyDB.getBoolean(AppConfig.UNINSTALL_APP));

        switch_uninstall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "EXPLANATION");
                startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.setting_layout_security_parent) {
            if (setting_layout_security_child.getVisibility() == View.GONE) {
                Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
                setting_layout_security_child.startAnimation(bottomUp);
                setting_layout_security_child.setVisibility(View.VISIBLE);
                setting_layout_general_child.setVisibility(View.GONE);
                setting_layout_magic_child.setVisibility(View.GONE);

                setting_layout_security_parent.setVisibility(View.GONE);
                setting_layout_general_parent.setVisibility(View.VISIBLE);
                setting_layout_magic_parent.setVisibility(View.VISIBLE);


            }
        }

        if (v.getId() == R.id.setting_layout_general_parent) {
            if (setting_layout_general_child.getVisibility() == View.GONE) {
                Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
                setting_layout_general_child.startAnimation(bottomUp);
                setting_layout_general_child.setVisibility(View.VISIBLE);
                setting_layout_security_child.setVisibility(View.GONE);
                setting_layout_magic_child.setVisibility(View.GONE);

                setting_layout_security_parent.setVisibility(View.VISIBLE);
                setting_layout_general_parent.setVisibility(View.GONE);
                setting_layout_magic_parent.setVisibility(View.VISIBLE);
            }
        }

        if (v.getId() == R.id.setting_layout_magic_parent) {
            if (setting_layout_magic_child.getVisibility() == View.GONE) {
                Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
                setting_layout_magic_child.startAnimation(bottomUp);
                setting_layout_magic_child.setVisibility(View.VISIBLE);
                setting_layout_security_child.setVisibility(View.GONE);
                setting_layout_general_child.setVisibility(View.GONE);

                setting_layout_security_parent.setVisibility(View.VISIBLE);
                setting_layout_general_parent.setVisibility(View.VISIBLE);
                setting_layout_magic_parent.setVisibility(View.GONE);


            }
        }

        if (v.getId() == R.id.arrow_up1) {
            setting_layout_security_child.setVisibility(View.GONE);
            setting_layout_general_child.setVisibility(View.GONE);
            setting_layout_magic_child.setVisibility(View.GONE);

            setting_layout_security_parent.setVisibility(View.VISIBLE);
        }

        if (v.getId() == R.id.arrow_up2) {
            setting_layout_security_child.setVisibility(View.GONE);
            setting_layout_general_child.setVisibility(View.GONE);
            setting_layout_magic_child.setVisibility(View.GONE);

            setting_layout_general_parent.setVisibility(View.VISIBLE);
        }

        if (v.getId() == R.id.arrow_up3) {
            setting_layout_security_child.setVisibility(View.GONE);
            setting_layout_general_child.setVisibility(View.GONE);
            setting_layout_magic_child.setVisibility(View.GONE);

            setting_layout_magic_parent.setVisibility(View.VISIBLE);
        }

        if (v.getId() == R.id.setting_set_security_question) {
            appConfigListener.onSuccess("setting_set_security_question", 2);
        }


        if (v.getId() == R.id.change_lock_pin) {
            appConfigListener.onAskQuestion("Success", 1);
        }
        if (v.getId() == R.id.change_lock_pattern) {
            appConfigListener.onAskQuestion("Success", 2);
        }

        if (v.getId() == R.id.setting_lock_time) {
            dialog.show();
        }

        if (v.getId() == R.id.setting_change_icon) {
            appConfigListener.onSuccess("setting_set_security_question", 3);
        }
        if (v.getId() == R.id.setting_observer) {
            appConfigListener.onSuccess("setting_set_security_question", 4);
        }

        if (v.getId() == R.id.setting_intruder) {
            getActivity().startActivity(new Intent(getActivity(), SpyActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDPM.isAdminActive(mDeviceAdmin)) {
            tinyDB.putBoolean(AppConfig.UNINSTALL_APP, true);
            switch_uninstall.setChecked(true);
        } else {
            switch_uninstall.setChecked(false);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        dialog.dismiss();
    }


    private class LockSettingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();

            tinyDB = new TinyDB(context);
            if (action.equals(ON_ITEM_CLICK_ACTION)) {
                LockAutoTime info = intent.getParcelableExtra("info");
                boolean isLast = intent.getBooleanExtra("isLast", true);
                if (isLast) {
                    lock_time.setText(info.getTitle());
                    tinyDB.putString(AppConfig.LOCK_APART_TITLE, info.getTitle());
                    tinyDB.putLong(AppConfig.LOCK_APART_MILLISECONDS, 0L);
                    tinyDB.putBoolean(AppConfig.LOCK_AUTO_SCREEN_TIME, false);
                } else {
                    lock_time.setText(info.getTitle());
                    tinyDB.putString(AppConfig.LOCK_APART_TITLE, info.getTitle());
                    tinyDB.putLong(AppConfig.LOCK_APART_MILLISECONDS, info.getTime());
                    tinyDB.putBoolean(AppConfig.LOCK_AUTO_SCREEN_TIME, true);
                }

                lock_time.setText(tinyDB.getString(AppConfig.LOCK_APART_TITLE));
                dialog.dismiss();
            }
        }
    }

    public int setPin() {
        if (tinyDB.getBoolean(AppConfig.IS_PATTERN_SET)) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLockSettingReceiver != null) {
            getActivity().unregisterReceiver(mLockSettingReceiver);
            mLockSettingReceiver = null;
        }
    }
}