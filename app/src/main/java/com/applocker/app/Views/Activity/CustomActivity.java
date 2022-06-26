package com.applocker.app.Views.Activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.CommLockInfoManager;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.IndicatorDots;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.PinLockListener;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.PinLockView;
import com.applocker.app.Utils.Pattern.PatternLockView;

public class CustomActivity extends BaseActivity {

    public static final String TAG = "CustomActivity";

    private PatternLockView mCircleLockView;
    private boolean result = false;

    private static final int PIN_LENGTH = 4;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    private TextView mTextTitle;

    private TextView app_name;
    private Drawable iconDrawable;
    private ImageView app_icon;

    private String pkgName = null;
    private String appLabel;
    private ApplicationInfo appInfo;
    private PackageManager packageManager;
    private CommLockInfoManager mLockInfoManager;

    private Bundle bundle = new Bundle();

    private int mTryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLocker.getInstance().doForCreate(this);
        resetFontScale(getResources().getConfiguration(), this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_custom);

        bundle = getIntent().getExtras();
        int value = bundle.getInt(AppConfig.SET_CUSTOME);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout main_setting = findViewById(R.id.main_setting);
        main_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(PremiumActivityDailog.class, null);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            }
        });

        app_icon = findViewById(R.id.app_icon);

        app_name = findViewById(R.id.app_name);

        initData();

        //Lock type
        if (value == 1) {

            mTextTitle = (TextView) findViewById(R.id.title);
            mTextTitle.setText(getString(R.string.pinlock_settitle));

            mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
            mIndicatorDots.setVisibility(View.VISIBLE);

            final PinLockListener pinLockListener = new PinLockListener() {

                @Override
                public void onComplete(String pin) {
                    setPin(pin, value);
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

            mPinLockView = (PinLockView) findViewById(R.id.pinlockView);
            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(pinLockListener);

            mPinLockView.setVisibility(View.VISIBLE);

            mPinLockView.setPinLength(PIN_LENGTH);
            mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        } else {

            mTextTitle = (TextView) findViewById(R.id.title);
            mTextTitle.setText(getString(R.string.pattern_settitle));
            mCircleLockView = (PatternLockView) findViewById(R.id.lock_view_circle);
            mCircleLockView.setVisibility(View.VISIBLE);
            if (tinyDB.getBoolean(AppConfig.PATTERN_VISIBLE)) {
                mCircleLockView.setPatternVisible(true);
            } else {
                mCircleLockView.setPatternVisible(false);
            }


            String pin = tinyDB.getString(AppConfig.PATTERN);

            mCircleLockView.setCallBack(new PatternLockView.CallBack() {
                @Override
                public int onFinish(PatternLockView.Password password) {

                    if (password.string.length() >= 9) {
                        result = true;
                        setPin(password.string, value);

                    } else {
                        result = false;
                        mTextTitle.setText(getString(R.string.pattern_lenth));

                        if (mTryCount == 1) {
                            mPinLockView.resetPinLockView();
                        } else if (mTryCount == 2) {
                            mPinLockView.resetPinLockView();
                        } else if (mTryCount > 2) {
                            //Take Self
                            onBackPressed();
                        }

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

        }

    }

    private void initData() {
        pkgName = bundle.getString(AppConfig.CUSTOM_APP);
        packageManager = getPackageManager();
        mLockInfoManager = new CommLockInfoManager(this);

        initLayoutBackground();
    }

    private void initLayoutBackground() {
        try {
            appInfo = packageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (appInfo != null) {
                iconDrawable = packageManager.getApplicationIcon(appInfo);
                appLabel = packageManager.getApplicationLabel(appInfo).toString();
                app_icon.setImageDrawable(iconDrawable);
                app_name.setText(appLabel);

                /*final Drawable icon = packageManager.getApplicationIcon(appInfo);
                mUnLockLayout.setBackgroundDrawable(icon);
                mUnLockLayout.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                mUnLockLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                                mUnLockLayout.buildDrawingCache();
                                int width = mUnLockLayout.getWidth(), height = mUnLockLayout.getHeight();
                                if (width == 0 || height == 0) {
                                    Display display = getWindowManager().getDefaultDisplay();
                                    Point size = new Point();
                                    display.getSize(size);
                                    width = size.x;
                                    height = size.y;
                                }
                                Bitmap bmp = LockUtil.drawableToBitmap(icon, width, height);
                                try {
                                    LockUtil.blur(CustomActivity.this, LockUtil.big(bmp), mUnLockLayout, width, height);
                                } catch (IllegalArgumentException ignore) {

                                }
                                return true;
                            }
                        });*/

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        bundle.putInt(AppConfig.SET_APP, 2);
        startActivity(AppsActivity.class, bundle);
        finish();
    }

    private void setPin(String pin, int status) {
        Log.d("Custom_PIN", pin);
        mLockInfoManager.lockCustomApplication(pkgName, pin, status);
        onBackPressed();
    }
}
