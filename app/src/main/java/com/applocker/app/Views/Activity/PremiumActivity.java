package com.applocker.app.Views.Activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.Pin.activity.PinActivity;
import com.applocker.app.Views.Dailog.PermissionActivity;

public class PremiumActivity extends BaseActivity {

    private ImageView close;
    TextView privacy_policy, terms_condition;
    private Button continueBtn;

    private Handler adHandler;
    private Runnable runnable;

    private Boolean status = false;
    private String buttonValue = "Continue";

    private boolean requestPermission = false;
    private boolean in_app = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_premium);

        tinyDB = new TinyDB(PremiumActivity.this);
        initializeBilling(PremiumActivity.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            in_app = extras.getBoolean(AppConfig.IN_APP);
        }

        close = findViewById(R.id.close);
        close.setVisibility(View.GONE);

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setText(buttonValue);


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SkuDetails skuDetails = getConsoleProductDeatils();
                if (skuDetails != null) {
                    lunchPruchaseFlow(PremiumActivity.this);
                } else {
                    Toast.makeText(PremiumActivity.this, "No Premium package Available", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {

        if (!haveIgnoreBatteryPermission()) {
            requestPermission = true;
        }

        adHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                close.setVisibility(View.VISIBLE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!haveIgnoreBatteryPermission() && requestPermission) {

                            if (!haveIgnoreBatteryPermission()) {
                                new AlertDialog.Builder(PremiumActivity.this)
                                        .setTitle("No Battery Restriction")
                                        .setMessage("Please select  No Restriction or Enable No Battery Restriction")
                                        .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                askForIgnoreBatteryPermission();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setCancelable(false)
                                        .show();
                            }


                        } else {

                            String manufacturer = "xiaomi";
                            if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {

                                if (!tinyDB.getBoolean("xiaomi_auto_restart")) {
                                    //this will open auto start screen where user can enable permission for your app

                                    new AlertDialog.Builder(PremiumActivity.this)
                                            .setTitle("Please Enable the additional permissions")
                                            .setMessage("Plese Enable Auto Restart Permission for running in background...")
                                            .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent1 = new Intent();
                                                    intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                                                    startActivity(intent1);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("Title", "Auto Restart Permission");
                                                    bundle.putString("Message", "Enable Auto Restart Permission.");
                                                    startActivity(PermissionActivity.class, bundle);
                                                    tinyDB.putBoolean("xiaomi_auto_restart", true);
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setCancelable(false)
                                            .show();


                                } else if (!tinyDB.getBoolean("xiaomi_background")) {

                                    new AlertDialog.Builder(PremiumActivity.this)
                                            .setTitle("Please Enable the additional permissions")
                                            .setMessage("Please check Display pop-up Window and Display pop-up Window while running in background...")
                                            .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                                                    intent.setClassName("com.miui.securitycenter",
                                                            "com.miui.permcenter.permissions.PermissionsEditorActivity");
                                                    intent.putExtra("extra_pkgname", getPackageName());
                                                    startActivity(intent);

                                                    tinyDB.putBoolean("xiaomi_background", true);
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setCancelable(false)
                                            .show();

                                } else {

                                    if (in_app) {
                                        startActivity(MainActivity.class, null);
                                    } else {
                                        startActivity(PinActivity.class, null);
                                    }

                                }


                            } else {
                                if (in_app) {
                                    startActivity(MainActivity.class, null);
                                } else {
                                    startActivity(PinActivity.class, null);
                                }
                            }

                        }

                    }
                });

            }
        };
        adHandler.postDelayed(runnable, 3000);

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (billingClient.getConnectionState() != 2) {
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                        // The BillingClient is ready. You can query purchases here.
                        showProducts(PremiumActivity.this);
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {

                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    BillingConnection(PremiumActivity.this);

                }
            });
        }


    }

    @Override
    protected void onPause() {
        adHandler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();

    }

    @Override
    protected void onStop() {
        adHandler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onStop();
    }

    @Override
    public void onBackPressed() {
    }
}