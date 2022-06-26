package com.applocker.app.Views.Fragments;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.R;
import com.applocker.app.Utils.Pin.fingerprint.FingerPrintListener;
import com.applocker.app.Utils.Pin.fingerprint.FingerprintHandler;
import com.applocker.app.Utils.Pin.util.Animate;

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


public class FingerPrintFragment extends Fragment {

    public final String TAG = "ScanFragment";

    private AppConfigListener appConfigListener;

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
    private int mTryCount = 0;

    private Button skip;


    public FingerPrintFragment(AppConfigListener appConfigListener) {
        this.appConfigListener = appConfigListener;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);


        mImageViewFingerView = (AppCompatImageView) view.findViewById(R.id.fingerView);
        mTextFingerText = (TextView) view.findViewById(R.id.fingerText);

        skip = (Button) view.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appConfigListener.onSuccess("Success", 1);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFingerprint = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.show_fingerprint);
            fingerprintToTick = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.fingerprint_to_tick);
            fingerprintToCross = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.fingerprint_to_cross);
        }

        checkForFingerPrint();

        return view;
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
                        appConfigListener.onSuccess("Success", 1);
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
                            //Take Self

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
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                //Get an instance of KeyguardManager and FingerprintManager//
                mKeyguardManager = (KeyguardManager) getActivity().getSystemService(getActivity().KEYGUARD_SERVICE);
                mFingerprintManager = (FingerprintManager) getActivity().getSystemService(getActivity().FINGERPRINT_SERVICE);

                //Check whether the user has granted your app the USE_FINGERPRINT permission//
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT)
                        != PackageManager.PERMISSION_GRANTED) {
                    /*mImageViewFingerView.setVisibility(View.GONE);*/
                    return;
                }

                if (!mFingerprintManager.hasEnrolledFingerprints()) {
                    /*mImageViewFingerView.setVisibility(View.GONE);*/
                    return;
                }

                //Check that the lockscreen is secured//
                if (!mKeyguardManager.isKeyguardSecure()) {
                    /*mImageViewFingerView.setVisibility(View.GONE);*/
                    return;
                } else {
                    try {
                        generateKey();
                        if (initCipher()) {
                            //If the mCipher is initialized successfully, then create a CryptoObject instance//
                            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);

                            // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                            // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                            FingerprintHandler helper = new FingerprintHandler(getActivity());
                            helper.startAuth(mFingerprintManager, mCryptoObject);
                            helper.setFingerPrintListener(fingerPrintListener);
                        }
                    } catch (FingerprintException e) {
                        Log.wtf(TAG, "Failed to generate key for fingerprint.", e);
                    }
                }
            } else {
                /*mImageViewFingerView.setVisibility(View.GONE);*/
            }
        } else {
            /*mImageViewFingerView.setVisibility(View.GONE);*/
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
}