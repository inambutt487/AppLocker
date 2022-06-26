package com.applocker.app.Views.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Manager.ServiceWorker;
import com.applocker.app.R;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.LoadApps;
import com.applocker.app.Views.Dailog.PermissionActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String MESSAGE_STATUS = "message_status";
    public static final String TAG = "MainActivity";
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ImageView menuImg;
    private ImageView textView1, textView2, textView3, textView4;
    private Bundle bundle = new Bundle();
    private float rating = 0;
    public LoadApps loadApps;

    private boolean permission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_main);

        tinyDB = new TinyDB(MainActivity.this);
        showNotification();
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        nvDrawer.setBackgroundResource(R.drawable.nav_gradient);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        menuImg = findViewById(R.id.menuImg);
        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        if (nvDrawer.getHeaderCount() > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            View headerLayout = nvDrawer.getHeaderView(0);

            TextView how_to_use = headerLayout.findViewById(R.id.how_to_use);
            how_to_use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(SlidesActivity.class, null);
                    mDrawer.closeDrawers();
                }
            });

            TextView share = headerLayout.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(ShareActivity.class, null);
                    mDrawer.closeDrawers();


                }
            });

            TextView privacy_policy = headerLayout.findViewById(R.id.privacy_policy);
            privacy_policy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(PrivacyActivity.class, null);
                    mDrawer.closeDrawers();

                }
            });

            TextView rate_us = headerLayout.findViewById(R.id.rate_us);
            rate_us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RateUs();
                    mDrawer.closeDrawers();

                }
            });

            TextView contact_us = headerLayout.findViewById(R.id.contact_us);
            contact_us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(FeedbackActivity.class, null);
                    mDrawer.closeDrawers();

                }
            });

            TextView more_app = headerLayout.findViewById(R.id.more_app);
            more_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                    mDrawer.closeDrawers();
                }
            });

            TextView quite = headerLayout.findViewById(R.id.quite);
            quite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDrawer.closeDrawers();
                    onBackPressed();

                }
            });

        }

        if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
            RelativeLayout main_setting = findViewById(R.id.main_setting);
            main_setting.setVisibility(View.VISIBLE);
            main_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PremiumActivityDailog.class, null);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });
        }

        if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
            RelativeLayout premium = findViewById(R.id.premium);
            premium.setVisibility(View.VISIBLE);
            premium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConfig.IN_APP, true);
                    startActivity(PremiumActivity.class, bundle);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });
        }


        textView1 = findViewById(R.id.textView1);
        textView1.setOnClickListener(this);
        textView2 = findViewById(R.id.textView2);
        textView2.setOnClickListener(this);
        textView3 = findViewById(R.id.textView3);
        textView3.setOnClickListener(this);
        textView4 = findViewById(R.id.textView4);
        textView4.setOnClickListener(this);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void showNotification() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            RemoteMessage msg = (RemoteMessage) extras.get("msg");

            if (msg == null) {

                if (extras.getString(getString(R.string.messageAction)) != null && extras.getString(getString(R.string.messageLink)) != null && extras.getString(getString(R.string.messageRate)) != null) {

                    String messagetitle, messageBody, messageIcon, messageAction, messageLink, messageRate, messageImage;
                    messagetitle = messageBody = messageIcon = messageAction = messageLink = messageRate = messageImage = null;

                    if (extras.getString(getString(R.string.messagetitle)) != null) {
                        messagetitle = extras.getString(getString(R.string.messagetitle));
                    }

                    if (extras.getString(getString(R.string.messageBody)) != null) {
                        messageBody = extras.getString(getString(R.string.messageBody));
                    }

                    if (extras.getString(getString(R.string.messageIcon)) != null) {
                        messageIcon = extras.getString(getString(R.string.messageIcon));
                    }

                    if (extras.getString(getString(R.string.messageAction)) != null) {
                        messageAction = extras.getString(getString(R.string.messageAction));
                    }

                    if (extras.getString(getString(R.string.messageLink)) != null) {
                        messageLink = extras.getString(getString(R.string.messageLink));
                    }

                    if (extras.getString(getString(R.string.messageRate)) != null) {
                        messageRate = extras.getString(getString(R.string.messageRate));
                    }

                    if (extras.getString(getString(R.string.messageImage)) != null) {
                        messageRate = extras.getString(getString(R.string.messageImage));
                    }

                    notificationShow(messagetitle, messageBody, messageIcon, messageAction, messageLink, messageRate, messageRate);


                }


            } else {

                if (msg.getData().get("Action") != null && msg.getData().get("Link") != null && msg.getData().get("Rate") != null) {

                    String dialogMessage, dialogTitle, dialogIcon, dialogAction, dialogLink, dialogRate, dialogImage;

                    dialogMessage = msg.getData().get("Message");
                    if (dialogMessage == null || dialogMessage.length() == 0) {
                        dialogMessage = "";
                    }

                    dialogTitle = msg.getData().get("Title");
                    if (dialogTitle == null || dialogTitle.length() == 0) {
                        dialogTitle = "";
                    }

                    dialogIcon = msg.getData().get("Icon");
                    if (dialogIcon == null || dialogIcon.length() == 0) {
                        dialogIcon = "";
                    }

                    dialogAction = msg.getData().get("Action");
                    if (dialogAction == null || dialogAction.length() == 0) {
                        dialogAction = "";
                    }

                    dialogLink = msg.getData().get("Link");
                    if (dialogLink == null || dialogLink.length() == 0) {
                        dialogLink = "";
                    }

                    dialogRate = msg.getData().get("Rate");
                    if (dialogRate == null || dialogRate.length() == 0) {
                        dialogRate = "";
                    }


                    dialogImage = msg.getData().get("Image");
                    if (dialogImage == null || dialogImage.length() == 0) {
                        dialogImage = "";
                    }

                    notificationShow(dialogTitle, dialogMessage, dialogIcon, dialogAction, dialogLink, dialogRate, dialogImage);
                }

            }


        }


    }

    private void notificationShow(String Title, String Message, String Image, String Action, String Link, String Rate, String FeatureImage) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notification_show);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (Title != null) {
            TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
            title.setText(Title);
        }

        if (Rate != null) {

            try {
                int i = Integer.parseInt(Rate);
                RatingBar rate = (RatingBar) dialog.findViewById(R.id.dialog_Rate);
                rate.setRating(Integer.valueOf(Rate));
            } catch (NumberFormatException ex) { // handle your exception
            }
        }

        if (Title != null) {
            TextView desc = (TextView) dialog.findViewById(R.id.dialog_desc);
            desc.setText(Message);
        }

        if (Image != null) {

            ImageView notificationIcon = (ImageView) dialog.findViewById(R.id.notification_icon);
            new AsyncTask<String, Integer, Drawable>() {

                @Override
                protected Drawable doInBackground(String... strings) {
                    Bitmap bmp = null;
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(Image).openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        bmp = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return new BitmapDrawable(bmp);
                }

                protected void onPostExecute(Drawable result) {

                    //Add image to ImageView

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notificationIcon.setImageDrawable(result);
                        }
                    });


                }


            }.execute();


            notificationIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            notificationIcon.setVisibility(View.VISIBLE);
        }


        if (FeatureImage != null) {

            ImageView dialog_image = (ImageView) dialog.findViewById(R.id.dialog_image);
            new AsyncTask<String, Integer, Drawable>() {

                @Override
                protected Drawable doInBackground(String... strings) {
                    Bitmap bmp = null;
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(FeatureImage).openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        bmp = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return new BitmapDrawable(bmp);
                }

                protected void onPostExecute(Drawable result) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog_image.setImageDrawable(result);
                        }
                    });


                }


            }.execute();


            dialog_image.setScaleType(ImageView.ScaleType.FIT_XY);
            dialog_image.setVisibility(View.VISIBLE);

        }

        Button dialog_go_to_link = (Button) dialog.findViewById(R.id.dialog_go_to_link);
        if (Action != null && Link != null && URLUtil.isValidUrl(Link)) {

            dialog_go_to_link.setText(Action);
            dialog_go_to_link.setVisibility(View.VISIBLE);
            dialog_go_to_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Link));
                    startActivity(browserIntent);
                    dialog.dismiss();
                }
            });


        }

        final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        exitDialog(MainActivity.this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.textView1) {
            if (!haveOverlayPermission() || !haveAppUsage()) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        permission = true;
                        askPermission();
                    }
                }, 500);
            } else {
                bundle.putInt(AppConfig.SET_APP, 0);
                startActivity(AppsActivity.class, bundle);

            }
        }

        if (v.getId() == R.id.textView2) {
            if (!haveOverlayPermission() || !haveAppUsage()) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        permission = true;
                        askPermission();
                    }
                }, 500);
            } else {
                bundle.putInt(AppConfig.SET_APP, 1);
                startActivity(AppsActivity.class, bundle);

            }
        }

        if (v.getId() == R.id.textView3) {
            if (!haveOverlayPermission() || !haveAppUsage()) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        permission = true;
                        askPermission();
                    }
                }, 500);
            } else {
                bundle.putInt(AppConfig.SET_APP, 2);
                startActivity(AppsActivity.class, bundle);

            }
        }

        if (v.getId() == R.id.textView4) {

            if (!haveOverlayPermission() || !haveAppUsage()) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        permission = true;
                        askPermission();
                    }
                }, 500);
            } else {

                bundle.putInt(AppConfig.SETTING, 1);
                startActivity(SettingActivity.class, bundle);

            }

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!isMyServiceRunning(LockService.class) && haveOverlayPermission() && haveAppUsage() && permission) {
            BackgroundManager.getInstance().init(this).startService(LockService.class);
            BackgroundManager.getInstance().init(this).startAlarmManager();
            /*startServiceViaWorker();*/
            tinyDB.putBoolean(AppConfig.LOCK_STATE, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void askPermission() {

        final Dialog permissiom = new Dialog(MainActivity.this);
        permissiom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        permissiom.setContentView(R.layout.permission_dialog);
        permissiom.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        permissiom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        permissiom.setCanceledOnTouchOutside(false);
        permissiom.setCancelable(false);

        final CheckBox permission_check1 = permissiom.findViewById(R.id.permission_check1);
        if (haveOverlayPermission()) {
            permission_check1.setChecked(true);
            permission_check1.setClickable(false);
        }

        /*permission_check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!haveOverlayPermission() && isChecked) {
                    askForSystemOverlayPermission();
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", "Overlay Permission");
                    bundle.putString("Message", "Draw on other apps.");
                    startActivity(PermissionActivity.class, bundle);
                    permissiom.dismiss();
                }
            }
        });*/

        final CheckBox permission_check2 = permissiom.findViewById(R.id.permission_check2);
        if (haveAppUsage()) {
            permission_check2.setChecked(true);
            permission_check2.setClickable(false);
        }

        /*permission_check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!haveAppUsage() && isChecked) {
                    askForAppUsagePermission();
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", "Usage Permission");
                    bundle.putString("Message", "Usage data access.");
                    startActivity(PermissionActivity.class, bundle);
                    permissiom.dismiss();
                }
            }
        });*/

        final Button done = permissiom.findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!haveOverlayPermission() && !haveAppUsage()) {
                    askForSystemOverlayPermission();
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", "Overlay Permission");
                    bundle.putString("Message", "Draw on other apps.");
                    startActivity(PermissionActivity.class, bundle);
                    permissiom.dismiss();
                }

                if (haveOverlayPermission() && !haveAppUsage()) {
                    askForAppUsagePermission();
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", "Usage Permission");
                    bundle.putString("Message", "Usage data access.");
                    startActivity(PermissionActivity.class, bundle);
                    permissiom.dismiss();
                }

            }
        });

        permissiom.show();

    }

    private void RateUs() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.rate_dialog);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        RatingBar ratingBar = dialog.findViewById(R.id.rate);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating = rating;
            }
        });
        dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating > 4) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    dialog.dismiss();
                } else if (rating == 0) {
                    Toast.makeText(MainActivity.this, "Kindly provide feedback!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Thank you for you feedback!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }


    public void startServiceViaWorker() {

        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        ServiceWorker.class,
                        16,
                        TimeUnit.MINUTES)
                        .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }
}