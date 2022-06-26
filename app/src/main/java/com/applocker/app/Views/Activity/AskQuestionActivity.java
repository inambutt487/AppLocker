package com.applocker.app.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Service.SpyCamera.CameraService;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.Pin.activity.CorePinActivity;
import com.applocker.app.Utils.Pin.fingerprint.FingerPrintListener;
import com.applocker.app.Utils.Pin.fingerprint.FingerprintHandler;
import com.applocker.app.Utils.Pin.util.Animate;
import com.applocker.app.Utils.Utils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AskQuestionActivity extends BaseActivity {

    public final String TAG = "AskQuestionActivity";

    private TinyDB tinyDB;
    private Context context;

    private Spinner questionsSpinner;
    private EditText answer;
    private Button confirmButton;
    private int questionNumber = 0;

    private int status = 0;

    private TextView mTextFingerText;
    private AppCompatImageView mImageViewFingerView;
    private static final String FINGER_PRINT_KEY = "ScanFragment";

    private AnimatedVectorDrawable showFingerprint;
    private AnimatedVectorDrawable fingerprintToTick;
    private AnimatedVectorDrawable fingerprintToCross;

    private Cipher mCipher;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;

    private Button nextBtn;
    private int mTryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_ask_question);

        context = AskQuestionActivity.this;
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


        confirmButton = (Button) findViewById(R.id.confirmButton);
        questionsSpinner = (Spinner) findViewById(R.id.questionsSpinner);
        answer = (EditText) findViewById(R.id.answer);

        List<String> list = new ArrayList<String>();
        list.add("Select your security question?");
        list.add("What is your pet name?");
        list.add("Who is your favorite teacher?");
        list.add("Who is your favorite actor?");
        list.add("Who is your favorite actress?");
        list.add("Who is your favorite cricketer?");
        list.add("Who is your favorite footballer?");

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionsSpinner.setAdapter(stringArrayAdapter);
        questionsSpinner.setSelection(0);

        questionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionNumber = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionNumber != 0 && !answer.getText().toString().isEmpty()) {

                    if (tinyDB.getInt(AppConfig.QUESTION_NUMBER) == questionNumber && tinyDB.getString(AppConfig.ANSWER).matches(answer.getText().toString())) {
                        Bundle bundle = getIntent().getExtras();
                        int value = bundle.getInt(AppConfig.LOAD_QUESTION);

                        if (value == 1) {
                            tinyDB.putString(AppConfig.PASSWORD, "");
                            tinyDB.putBoolean(AppConfig.IS_PASSWORD_SET, false);
                            tinyDB.putBoolean(AppConfig.LOCK_SETTING, false);
                        } else {
                            tinyDB.putString(AppConfig.PATTERN, "");
                            tinyDB.putBoolean(AppConfig.IS_PATTERN_SET, false);
                            tinyDB.putBoolean(AppConfig.LOCK, false);
                            tinyDB.putBoolean(AppConfig.LOCK_SETTING, false);
                        }

                        bundle.putInt(AppConfig.LOAD_QUESTION, value);
                        startActivity(SetPasswordActivity.class, bundle);
                        finish();

                    } else {
                        Toast.makeText(context, "Your question and answer didn't matches", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                } else {
                    Toast.makeText(context, "Please select a question and write an answer", Toast.LENGTH_SHORT).show();

                    mTryCount++;

                    if (mTryCount == 1) {

                    } else if (mTryCount == 2) {

                    } else if (mTryCount > 2) {
                        TakeSelfi();
                        onBackPressed();
                    }

                }


            }
        });

        mImageViewFingerView = (AppCompatImageView) findViewById(R.id.fingerView);
        mTextFingerText = (TextView) findViewById(R.id.fingerText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFingerprint = (AnimatedVectorDrawable) context.getDrawable(R.drawable.show_fingerprint);
            fingerprintToTick = (AnimatedVectorDrawable) context.getDrawable(R.drawable.fingerprint_to_tick);
            fingerprintToCross = (AnimatedVectorDrawable) context.getDrawable(R.drawable.fingerprint_to_cross);
        }

        checkForFingerPrint();
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
                        startActivity(ConfigActivity.class, null);
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

                        } else if (mTryCount == 2) {

                        } else if (mTryCount > 2) {
                            TakeSelfi();
                            onBackPressed();
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
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                //Get an instance of KeyguardManager and FingerprintManager//
                mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
                mFingerprintManager = (FingerprintManager) context.getSystemService(context.FINGERPRINT_SERVICE);

                //Check whether the user has granted your app the USE_FINGERPRINT permission//
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                if (!mFingerprintManager.hasEnrolledFingerprints()) {
                    return;
                }

                //Check that the lockscreen is secured//
                if (!mKeyguardManager.isKeyguardSecure()) {
                    return;
                } else {
                    try {
                        generateKey();
                        if (initCipher()) {
                            //If the mCipher is initialized successfully, then create a CryptoObject instance//
                            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);

                            // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                            // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                            FingerprintHandler helper = new FingerprintHandler(context);
                            helper.startAuth(mFingerprintManager, mCryptoObject);
                            helper.setFingerPrintListener(fingerPrintListener);
                        }
                    } catch (FingerprintException e) {
                        Log.wtf(TAG, "Failed to generate key for fingerprint.", e);
                    }
                }
            }
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

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    public void TakeSelfi() {
        if (tinyDB.getBoolean(AppConfig.SPY)) {
            BackgroundManager.getInstance().init(AskQuestionActivity.this).startService(CameraService.class);
        }else{
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}