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

import androidx.fragment.app.Fragment;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.R;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.IndicatorDots;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.PinLockListener;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.PinLockView;
import com.applocker.app.Utils.Pin.util.Utils;
import com.applocker.app.Views.Activity.ConfigActivity;
import com.applocker.app.Views.Activity.SetPasswordActivity;


public class PinFragment extends Fragment {

    public AppConfigListener appConfigListener;

    public static final String TAG = "PinFragment";
    private static final int PIN_LENGTH = 4;

    private TinyDB tinyDB;

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mTextTitle;
    private TextView mTextAttempts;

    private boolean mSetPin = true;
    private String mFirstPin = "";
    private int mTryCount = 0;

    private Button use_pattern;


    public PinFragment(AppConfigListener appConfigListener) {
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
        View view = inflater.inflate(R.layout.fragment_pin, container, false);

        tinyDB.putBoolean(AppConfig.IS_PASSWORD_SET, false);
        tinyDB.putString(AppConfig.PASSWORD, "");
        mSetPin = true;

        if (getActivity() instanceof ConfigActivity) {
            //do something
            use_pattern = (Button) view.findViewById(R.id.use_pattern);
            use_pattern.setVisibility(View.VISIBLE);
            use_pattern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appConfigListener.onSuccess("Back", 0);
                }
            });
        }

        mTextAttempts = (TextView) view.findViewById(R.id.attempts);
        mTextTitle = (TextView) view.findViewById(R.id.title);
        mIndicatorDots = (IndicatorDots) view.findViewById(R.id.indicator_dots);


        if (mSetPin) {
            changeLayoutForSetPin();
        } else {
            String pin = getPinFromSharedPreferences();
            if (pin.equals("") || pin.isEmpty() || pin == null) {
                changeLayoutForSetPin();
                mSetPin = true;
            }
        }

        final PinLockListener pinLockListener = new PinLockListener() {

            @Override
            public void onComplete(String pin) {
                if (mSetPin) {
                    setPin(pin);
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }

        };

        mPinLockView = (PinLockView) view.findViewById(R.id.pinlockView);
        mIndicatorDots = (IndicatorDots) view.findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(pinLockListener);

        mPinLockView.setPinLength(PIN_LENGTH);

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        return view;
    }

    private void changeLayoutForSetPin() {
        mTextTitle.setText(getString(R.string.pinlock_settitle));
    }

    private void writePinToSharedPreferences(String pin) {
        tinyDB.putString(AppConfig.PASSWORD, Utils.sha256(pin));
        tinyDB.putBoolean(AppConfig.IS_PASSWORD_SET, true);
        tinyDB.putBoolean(AppConfig.LOCK_SETTING, true);
        appConfigListener.onSuccess("Success", 1);
    }

    private String getPinFromSharedPreferences() {
        return tinyDB.getString(AppConfig.PASSWORD);
    }

    private void setPin(String pin) {

        if (mFirstPin.equals("")) {
            mFirstPin = pin;
            mTextTitle.setText(getString(R.string.pinlock_secondPin));
            mPinLockView.resetPinLockView();
            use_pattern.setVisibility(View.GONE);
        } else {

            if (pin.equals(mFirstPin)) {
                writePinToSharedPreferences(pin);
            } else {
                mFirstPin = "";
                mTextTitle.setText(getString(R.string.pinlock_settitle));

                shake();
                mTextAttempts.setText(getString(R.string.pinlock_tryagain));
                mPinLockView.resetPinLockView();
                use_pattern.setVisibility(View.VISIBLE);
            }
        }
    }

    private void shake() {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(mPinLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0).setDuration(1000);
        objectAnimator.start();

        BaseActivity.Vibrate();
    }
}