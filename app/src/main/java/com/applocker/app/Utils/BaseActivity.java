package com.applocker.app.Utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.applocker.app.BuildConfig;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Views.Activity.MainActivity;
import com.applocker.app.Views.Dailog.ExitDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity {


    public static String Token = null;

    public static Vibrator mVibrator;

    public static int permission_VIBRATE = 102;
    public static int permission_CAMERA = 100;
    public static int permission_STORAGE = 101;
    public static int permission_SPY = 103;

    public static int permission_OVERLAY = 300;
    public static int permission_USAGE = 301;

    private final String TAG = "BASECLASS";
    private int responseCode;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    public static BillingClient billingClient;
    private SkuDetails ConsoleProductDeatils = null;
    public static TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        tinyDB.putBoolean(AppConfig.FINGER, haveFingerPrint());
        tinyDB.putBoolean(AppConfig.NEW_APP_INSTALL, true);
        tinyDB.putBoolean(AppConfig.CLEAR_CACHE, true);
    }

    public static void setStatusBar(Activity activity, int statusBarColorId, int navBarColorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(statusBarColorId));
            window.setNavigationBarColor(activity.getResources().getColor(navBarColorId));
        }
    }

    public void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void initializeBilling(final Context context) {

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.

                if (responseCode == BillingClient.BillingResponseCode.OK
                        && purchases != null) {


                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase, context);
                    }


                } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {


                } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

                    PurchaseHistory_Record();

                } else {


                }


            }
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


    }

    public void BillingConnection(final Context context) {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showProducts(context);
                }

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_TIMEOUT) {
                    BillingConnection(context);
                }

            }

            @Override
            public void onBillingServiceDisconnected() {

                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                BillingConnection(context);

            }
        });

    }

    public void showProducts(Context context) {

        ArrayList<String> skuList = new ArrayList<>();
        /*if (BuildConfig.DEBUG) {
            skuList.add("android.test.purchased");
        } else {
            skuList.add(BuildConfig.APPLICATION_ID);
        }*/

        skuList.add(BuildConfig.APPLICATION_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);


        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {


                        Log.d(TAG, "onSkuDetailsResponse: Products List " + skuDetailsList + "Response Code" + billingResult.getResponseCode());

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                            ConsoleProductDeatils = skuDetailsList.get(0);

                        }

                    }
                });

    }

    public void lunchPruchaseFlow(Activity activity) {

        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(getConsoleProductDeatils())
                .build();

        responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
    }

    public void handlePurchase(final Purchase purchase, final Context context) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.

                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {


                        //Update Ads Show False
                        if (!purchase.isAcknowledged()) {

                            tinyDB.putBoolean(AppConfig.PURCHASE, true);
                           /* tinyDB.putString("getOrderId", purchase.getOrderId());
                            tinyDB.putString("getPackageName", purchase.getPackageName());
                            tinyDB.putString("getPurchaseTime", "" + purchase.getPurchaseTime());*/

                            AcknowledgePurchaseParams acknowledgePurchaseParams =
                                    AcknowledgePurchaseParams.newBuilder()
                                            .setPurchaseToken(purchase.getPurchaseToken())
                                            .build();
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                        }
                    }


                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    public void setConsoleProductDeatils(SkuDetails consoleProductDeatils) {
        ConsoleProductDeatils = consoleProductDeatils;
    }

    public void PurchaseHistory_Record() {

        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(BillingResult billingResult,
                                                          List<PurchaseHistoryRecord> purchasesList) {

                        if (purchasesList.size() > 0 && purchasesList != null) {

                            for (PurchaseHistoryRecord purchase : purchasesList) {

                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                    ArrayList<String> arrayList = purchase.getSkus();

                                    for (String sku : arrayList) {

                                        if (sku.matches(BuildConfig.APPLICATION_ID)) {

                                            //Update Ads Show False
                                            tinyDB.putBoolean(AppConfig.PURCHASE, true);
                                            tinyDB.putBoolean(AppConfig.OWN, true);
                                        }
                                    }

                                }

                            }


                        }


                    }
                });
    }

    public SkuDetails getConsoleProductDeatils() {
        if (ConsoleProductDeatils != null)
            return ConsoleProductDeatils;
        else return ConsoleProductDeatils = null;

    }

    public void startActivity(Class<?> calledActivity, Bundle bundle) {
        Intent myIntent = new Intent(this, calledActivity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null)
            myIntent.putExtras(bundle);
        this.startActivity(myIntent);
    }

    public void showToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public void resetFontScale(Configuration configuration, Context context) {
        configuration.fontScale = 1f;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(AppCompatActivity.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        context.getResources().updateConfiguration(configuration, metrics);
        Log.d("APP", "Configurations updated");

    }

    public void exitDialog(Activity activity) {
        if (activity instanceof MainActivity) {
            ExitDialog dialog = new ExitDialog(activity);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }


    }

    public boolean isConnected() {
        try {
            String command = "ping -c 1 google.com";
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (Exception e) {
            return false;
        }

    }

    public void FirebaseToken(final Context context) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        setToken(task.getResult());

                        // Log and toast
                        Log.d("MyToken", getToken());

                    }

                });
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnectedOrConnecting())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnectedOrConnecting())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean havePurchase() {
        try {
            tinyDB = new TinyDB(this);
            return tinyDB.getBoolean("isPurchased");
        } catch (Exception e) {
            return false;
        }

    }

    public void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    public Boolean haveOverlayPermission() {
        return Settings.canDrawOverlays(getApplicationContext());
    }

    public void askForAppUsagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !haveAppUsage()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    public void askForIgnoreBatteryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                }
            } catch (Exception e) {

            }
        }
    }

    public boolean haveIgnoreBatteryPermission() {

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(packageName);

    }

    public boolean haveAppUsage() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean hasVibrateGranted(Activity context) {
        int result = ContextCompat.checkSelfPermission(context, VIBRATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestVibratePermission(Activity context) {
        ActivityCompat.requestPermissions(context, new String[]{VIBRATE}, permission_VIBRATE);
    }

    public boolean haveFingerPrint() {
        Boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
            // User hasn't enrolled any fingerprints to authenticate with
            // Everything is ready for fingerprint authentication
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
                result = false;
            } else result = fingerprintManager.hasEnrolledFingerprints();
        }

        return result;
    }

    public static void Vibrate() {
        if (tinyDB.getBoolean(AppConfig.VIBRATE)) {
            try {
                mVibrator.vibrate(20);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasStoragePermissionGranted(Activity context) {
        int result1 = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity context) {
        //below android 11
        ActivityCompat.requestPermissions(context, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, permission_STORAGE);
    }

    public static boolean hasCameraPermissionGranted(Activity context) {
        int result1 = ContextCompat.checkSelfPermission(context, CAMERA);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCameraPermission(Activity context) {
        //below android 11
        ActivityCompat.requestPermissions(context, new String[]{CAMERA}, permission_CAMERA);
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppLocker.getInstance().doForFinish(this);
    }


    public final void clear() {
        super.finish();
    }
}
