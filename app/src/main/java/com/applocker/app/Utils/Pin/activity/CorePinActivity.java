package com.applocker.app.Utils.Pin.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.CommLockInfoManager;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Service.LockService;
import com.applocker.app.Service.SpyCamera.CameraService;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.IndicatorDots;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.PinLockListener;
import com.applocker.app.Utils.Pin.andrognito.pinlockview.PinLockView;
import com.applocker.app.Utils.Pin.fingerprint.FingerPrintListener;
import com.applocker.app.Utils.Pin.fingerprint.FingerprintHandler;
import com.applocker.app.Utils.Pin.util.Animate;
import com.applocker.app.Utils.Pin.util.Utils;
import com.applocker.app.Utils.Pattern.PatternLockView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CorePinActivity extends BaseActivity {

    public static final String FINISH_UNLOCK_THIS_APP = "finish_unlock_this_app";
    public static final String TAG = "CorePinActivity";

    private PatternLockView mCircleLockView;
    private boolean result = false;

    private boolean result_lock = false;


    private static final int PIN_LENGTH = 4;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    private TextView mTextTitle;
    private TextView mTextAttempts;

    private int mTryCount = 0;

    private static final String FINGER_PRINT_KEY = "FingerPrintKey";
    private KeyguardManager mKeyguardManager;

    private Cipher mCipher;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintManager mFingerprintManager;

    private TextView mTextFingerText;
    private AppCompatImageView mImageViewFingerView;

    private AnimatedVectorDrawable showFingerprint;
    private AnimatedVectorDrawable fingerprintToTick;
    private AnimatedVectorDrawable fingerprintToCross;
    private GestureUnlockReceiver mGestureUnlockReceiver;

    private String pkgName = null;
    private String actionFrom = null;

    private TextView app_name, codeLock;
    private Drawable iconDrawable;
    private String appLabel;
    private ImageView mUnLockIcon;
    private ApplicationInfo appInfo;
    private PackageManager packageManager;
    private CommLockInfoManager mLockInfoManager;

    private Button forgot_lock;

    private String app_pin = "";

    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_core);
        Log.d(TAG, "Start");

        tinyDB = new TinyDB(CorePinActivity.this);
        mGestureUnlockReceiver = new GestureUnlockReceiver();
        IntentFilter filter = new IntentFilter();
        //  filter.addAction(UnLockMenuPopWindow.UPDATE_LOCK_VIEW);
        filter.addAction(FINISH_UNLOCK_THIS_APP);
        registerReceiver(mGestureUnlockReceiver, filter);


        mUnLockIcon = findViewById(R.id.app_icon);
        app_name = findViewById(R.id.app_name);
        codeLock = findViewById(R.id.codeLock);
        forgot_lock = findViewById(R.id.forgot_lock);
        forgot_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (result_lock) {
                    result_lock = false;
                } else {
                    result_lock = true;
                }

                loadUI(result_lock);
            }
        });

        mTextAttempts = (TextView) findViewById(R.id.attempts);
        mTextTitle = (TextView) findViewById(R.id.title);

        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView = (PinLockView) findViewById(R.id.pinlockView);
        mCircleLockView = (PatternLockView) findViewById(R.id.lock_view_circle);

        //Load UI
        loadUI();

        //Enable finger
        if (tinyDB.getBoolean(AppConfig.FINGER)) {

            mImageViewFingerView = (AppCompatImageView) findViewById(R.id.fingerView);
            mImageViewFingerView.setVisibility(View.VISIBLE);
            mTextFingerText = (TextView) findViewById(R.id.fingerText);
            mTextFingerText.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showFingerprint = (AnimatedVectorDrawable) getDrawable(R.drawable.show_fingerprint);
                fingerprintToTick = (AnimatedVectorDrawable) getDrawable(R.drawable.fingerprint_to_tick);
                fingerprintToCross = (AnimatedVectorDrawable) getDrawable(R.drawable.fingerprint_to_cross);
            }


            checkForFingerPrint();
        }


    }

    private boolean initData() {
        pkgName = getIntent().getStringExtra(AppConfig.LOCK_PACKAGE_NAME);
        actionFrom = getIntent().getStringExtra(AppConfig.LOCK_FROM);
        packageManager = getPackageManager();
        mLockInfoManager = new CommLockInfoManager(this);

        if (mLockInfoManager.isMultiLock(pkgName)) {

            app_pin = mLockInfoManager.getAppPin(pkgName);
            codeLock.setVisibility(View.VISIBLE);

            if (mLockInfoManager.ChangeLockPin(pkgName)) {
                codeLock.setText("Custom Pattern Lock Active");
                result_lock = true;
            } else {
                codeLock.setText("Custom Pin Lock Active");
                result_lock = false;
            }

            forgot_lock.setVisibility(View.VISIBLE);

        } else {
            codeLock.setVisibility(View.GONE);

            if (tinyDB.getBoolean(AppConfig.LOCK)) {
                result_lock = true;
            } else {
                result_lock = false;
            }
        }

        initLayoutBackground();

        return result_lock;
    }

    private void initLayoutBackground() {
        try {
            appInfo = packageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (appInfo != null) {
                iconDrawable = packageManager.getApplicationIcon(appInfo);
                appLabel = packageManager.getApplicationLabel(appInfo).toString();
                mUnLockIcon.setImageDrawable(iconDrawable);
                app_name.setText(appLabel);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            mKeyStore.load(null);

            //Initialize the KeyGenerator//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mKeyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(FINGER_PRINT_KEY,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }

            //Generate the key//
            mKeyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our mCipher//
    public boolean initCipher() {
        try {
            //Obtain a mCipher instance and configure it with the properties required for fingerprint authentication//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            Log.e(TAG, "Failed to get Cipher");
            return false;
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(FINGER_PRINT_KEY,
                    null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the mCipher has been initialized successfully//
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to init Cipher");
            return false;
        }
    }


    private String getPinFromSharedPreferences() {
        return tinyDB.getString(AppConfig.PASSWORD);
    }

    private void checkPin(String pin) {
        if (Utils.sha256(pin).equals(getPinFromSharedPreferences()) || pin.equals(app_pin)) {
            unLockCore();
        } else {

            shake();
            mTextAttempts.setText(getString(R.string.pinlock_wrongpin));
            mPinLockView.resetPinLockView();

            mTryCount++;

            if (mTryCount == 1) {
                mTextAttempts.setText(getString(R.string.pinlock_firsttry));
                mPinLockView.resetPinLockView();
            } else if (mTryCount == 2) {
                mTextAttempts.setText(getString(R.string.pinlock_secondtry));
                mPinLockView.resetPinLockView();
            } else if (mTryCount > 2) {
                //Take Self
                TakeSelfi();
            }
        }
    }

    private boolean checkPattern(String password) {

        if (password.equals(tinyDB.getString(AppConfig.PATTERN)) || password.equals(app_pin)) {
            unLockCore();
            return true;
        } else {

            shake();
            mTextAttempts.setText(getString(R.string.pinlock_wrongpin));

            mTryCount++;

            if (mTryCount == 1) {
                mTextAttempts.setText(getString(R.string.pinlock_firsttry));
                mCircleLockView.reset();
            } else if (mTryCount == 2) {
                mTextAttempts.setText(getString(R.string.pinlock_secondtry));
                mCircleLockView.reset();
            } else if (mTryCount > 2) {
                //Take Self
                TakeSelfi();
            }

            return false;
        }
    }

    private void shake() {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(mPinLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0).setDuration(1000);
        objectAnimator.start();

        Vibrate();
    }

    private void unLockCore() {

        tinyDB.putLong(AppConfig.LOCK_CURR_MILLISECONDS, System.currentTimeMillis());
        tinyDB.putString(AppConfig.LOCK_LAST_LOAD_PKG_NAME, pkgName);

        //Send the last unlocked time to the app lock service
        Intent intent = new Intent(LockService.UNLOCK_ACTION);
        intent.putExtra(LockService.LOCK_SERVICE_LASTTIME, System.currentTimeMillis());
        intent.putExtra(LockService.LOCK_SERVICE_LASTAPP, pkgName);
        sendBroadcast(intent);

        mLockInfoManager.unlockCommApplication(pkgName);
        finish();
    }

    private void checkForFingerPrint() {

        final FingerPrintListener fingerPrintListener = new FingerPrintListener() {

            @Override
            public void onSuccess() {

                Animate.animate(mImageViewFingerView, fingerprintToTick);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unLockCore();
                    }
                }, 750);
            }

            @Override
            public void onFailed() {
                Animate.animate(mImageViewFingerView, fingerprintToCross);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animate.animate(mImageViewFingerView, showFingerprint);

                        mTryCount++;

                        if (mTryCount == 1) {
                            mTextAttempts.setText(getString(R.string.pinlock_firsttry));
                            mPinLockView.resetPinLockView();
                        } else if (mTryCount == 2) {
                            mTextAttempts.setText(getString(R.string.pinlock_secondtry));
                            mPinLockView.resetPinLockView();
                        } else if (mTryCount > 2) {
                            //Take Self
                            TakeSelfi();
                        }

                    }
                }, 750);
            }

            @Override
            public void onError(CharSequence errorString) {

            }

            @Override
            public void onHelp(CharSequence helpString) {

            }

        };

        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                //Get an instance of KeyguardManager and FingerprintManager//
                mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                //Check whether the user has granted your app the USE_FINGERPRINT permission//
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If your app doesn't have this permission, then display the following text//
//                Toast.makeText(EnterPinActivity.this, "Please enable the fingerprint permission", Toast.LENGTH_LONG).show();
                    mImageViewFingerView.setVisibility(View.GONE);
//                    mTextFingerText.setVisibility(View.GONE);
                    return;
                }

                //Check that the user has registered at least one fingerprint//
                if (!mFingerprintManager.hasEnrolledFingerprints()) {
                    // If the user hasn’t configured any fingerprints, then display the following message//
//                Toast.makeText(EnterPinActivity.this,
//                        "No fingerprint configured. Please register at least one fingerprint in your device's Settings",
//                        Toast.LENGTH_LONG).show();
                    mImageViewFingerView.setVisibility(View.GONE);
//                    mTextFingerText.setVisibility(View.GONE);
                    return;
                }

                //Check that the lockscreen is secured//
                if (!mKeyguardManager.isKeyguardSecure()) {
                    // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
//                Toast.makeText(EnterPinActivity.this, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_LONG).show();
                    mImageViewFingerView.setVisibility(View.GONE);
//                    mTextFingerText.setVisibility(View.GONE);
                    return;
                } else {
                    try {
                        generateKey();
                        if (initCipher()) {
                            //If the mCipher is initialized successfully, then create a CryptoObject instance//
                            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);

                            // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                            // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                            FingerprintHandler helper = new FingerprintHandler(this);
                            helper.startAuth(mFingerprintManager, mCryptoObject);
                            helper.setFingerPrintListener(fingerPrintListener);
                        }
                    } catch (FingerprintException e) {
                        Log.wtf(TAG, "Failed to generate key for fingerprint.", e);
                    }
                }
            } else {
                mImageViewFingerView.setVisibility(View.GONE);
//                mTextFingerText.setVisibility(View.GONE);
            }
        } else {
            mImageViewFingerView.setVisibility(View.GONE);
//            mTextFingerText.setVisibility(View.GONE);
        }
    }


    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGestureUnlockReceiver);
    }

    @Override
    public void onBackPressed() {
    }

    private class GestureUnlockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action.equals(FINISH_UNLOCK_THIS_APP)) {
                finish();
            }
        }
    }

    public void TakeSelfi() {
        if (tinyDB.getBoolean(AppConfig.SPY)) {
            BackgroundManager.getInstance().init(CorePinActivity.this).startService(CameraService.class);
        }
    }


    private void changeLock(boolean aBoolean) {

        mIndicatorDots.setVisibility(View.GONE);
        mPinLockView.setVisibility(View.GONE);
        mPinLockView.resetPinLockView();
        mCircleLockView.setVisibility(View.GONE);
        mCircleLockView.reset();

        if (aBoolean) {
            mCircleLockView.setVisibility(View.VISIBLE);
        } else {
            mIndicatorDots.setVisibility(View.VISIBLE);
            mPinLockView.setVisibility(View.VISIBLE);
        }
    }

    public void loadUI() {

        mIndicatorDots.setVisibility(View.GONE);
        mPinLockView.setVisibility(View.GONE);
        mCircleLockView.setVisibility(View.GONE);

        if (!initData()) {

            mIndicatorDots.setVisibility(View.VISIBLE);
            final PinLockListener pinLockListener = new PinLockListener() {

                @Override
                public void onComplete(String pin) {
                    checkPin(pin);
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

            mPinLockView.setVisibility(View.VISIBLE);

            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(pinLockListener);

            mPinLockView.setPinLength(PIN_LENGTH);
            mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        } else {

            mCircleLockView.setVisibility(View.VISIBLE);

            if (tinyDB.getBoolean(AppConfig.PATTERN_VISIBLE)) {
                mCircleLockView.setPatternVisible(true);
            } else {
                mCircleLockView.setPatternVisible(false);
            }

            mTextTitle.setText(getString(R.string.pattern_title));

            mCircleLockView.setCallBack(new PatternLockView.CallBack() {
                @Override
                public int onFinish(PatternLockView.Password password) {

                    if (password.string.length() >= 9) {

                        result = checkPattern(password.string);

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

        }
    }

    public void loadUI(boolean status) {

        mIndicatorDots.setVisibility(View.GONE);
        mPinLockView.setVisibility(View.GONE);
        mCircleLockView.setVisibility(View.GONE);

        if (!status) {

            mIndicatorDots.setVisibility(View.VISIBLE);
            final PinLockListener pinLockListener = new PinLockListener() {

                @Override
                public void onComplete(String pin) {
                    checkPin(pin);
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

            mPinLockView.setVisibility(View.VISIBLE);

            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(pinLockListener);

            mPinLockView.setPinLength(PIN_LENGTH);
            mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        } else {

            mCircleLockView.setVisibility(View.VISIBLE);

            if (tinyDB.getBoolean(AppConfig.PATTERN_VISIBLE)) {
                mCircleLockView.setPatternVisible(true);
            } else {
                mCircleLockView.setPatternVisible(false);
            }

            mTextTitle.setText(getString(R.string.pattern_title));

            mCircleLockView.setCallBack(new PatternLockView.CallBack() {
                @Override
                public int onFinish(PatternLockView.Password password) {

                    if (password.string.length() >= 9) {

                        result = checkPattern(password.string);

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

        }
    }
}
