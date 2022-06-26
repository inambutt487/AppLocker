package com.applocker.app.Views.Fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.R;
import com.applocker.app.Utils.Pattern.PatternLockView;
import com.applocker.app.Views.Activity.ConfigActivity;


public class PatternFragment extends Fragment {


    public AppConfigListener appConfigListener;
    public static final String TAG = "PatternFragment";

    private TextView mTextTitle;
    private TextView mTextAttempts;

    private boolean mSetPattern = true;
    private boolean result = false;
    private String mFirstPin = "";

    private int mTryCount = 0;
    private TinyDB tinyDB;
    private PatternLockView mCircleLockView;

    private Button use_pin;


    public PatternFragment(AppConfigListener appConfigListener) {
        this.appConfigListener = appConfigListener;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(getActivity());
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pattern, container, false);


        tinyDB.putBoolean(AppConfig.IS_PATTERN_SET, false);
        tinyDB.putString(AppConfig.PATTERN, "");
        mSetPattern = true;

        mCircleLockView = (PatternLockView) view.findViewById(R.id.lock_view_circle);
        if (tinyDB.getBoolean(AppConfig.PATTERN_VISIBLE)) {
            mCircleLockView.setPatternVisible(true);
        } else {
            mCircleLockView.setPatternVisible(false);
        }
        mTextTitle = (TextView) view.findViewById(R.id.title);

        if (getActivity() instanceof ConfigActivity) {
            //do something
            use_pin = (Button) view.findViewById(R.id.use_pin);
            use_pin.setVisibility(View.VISIBLE);
            use_pin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appConfigListener.onSuccess("Skip", 1);
                }
            });
        }

        mTextAttempts = (TextView) view.findViewById(R.id.attempts);
        mTextTitle.setText(getString(R.string.pattern_settitle));


        mCircleLockView.setCallBack(new PatternLockView.CallBack() {
            @Override
            public int onFinish(PatternLockView.Password password) {

                if (password.string.length() >= 9) {

                    if (mSetPattern) {
                        mFirstPin = password.string;
                        mSetPattern = false;
                        mTextTitle.setText(getString(R.string.pattern_secondPin));
                        mCircleLockView.reset();
                        use_pin.setVisibility(View.GONE);
                        result = true;
                    } else {

                        if (password.string.equals(mFirstPin) && !mSetPattern) {
                            tinyDB.putString(AppConfig.PATTERN, password.string);
                            tinyDB.putBoolean(AppConfig.IS_PATTERN_SET, true);
                            tinyDB.putBoolean(AppConfig.LOCK, true);
                            tinyDB.putBoolean(AppConfig.LOCK_SETTING, true);
                            mSetPattern = true;
                            result = true;
                            appConfigListener.onSuccess("Success", 2);
                        } else {

                            mSetPattern = true;
                            mFirstPin = "";


                            use_pin.setVisibility(View.VISIBLE);
                            shake();
                            mTextAttempts.setText(getString(R.string.pattern_tryagain));
                            mCircleLockView.reset();

                            result = false;
                        }
                    }

                } else {
                    result = false;
                    mTextTitle.setText(getString(R.string.pattern_lenth));
                }

                if (result) {
                    return PatternLockView.CODE_PASSWORD_CORRECT;
                } else {
                    return PatternLockView.CODE_PASSWORD_ERROR;
                }

            }
        });

        mCircleLockView.setOnNodeTouchListener(new PatternLockView.OnNodeTouchListener() {
            @Override
            public void onNodeTouched(int NodeId) {
                Log.d(TAG, "node " + NodeId + " has touched!");
            }
        });


        return view;
    }


    private void shake() {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(mCircleLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0).setDuration(1000);
        objectAnimator.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // DO NOT FORGET TO CALL IT!
        mCircleLockView.stopPasswordAnim();
    }
}