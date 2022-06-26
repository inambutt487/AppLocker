package com.applocker.app.Views.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Interface.AppConfigListener;
import com.applocker.app.Interface.PermissionListener;
import com.applocker.app.Model.Page;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.MyViewPager;
import com.applocker.app.Views.Fragments.PatternFragment;
import com.applocker.app.Views.Fragments.PinFragment;
import com.applocker.app.Views.Fragments.QuestionFragment;
import com.applocker.app.Views.Fragments.FingerPrintFragment;
import com.applocker.app.Views.Fragments.SettingFragment;

import java.util.ArrayList;

public class ConfigActivity extends BaseActivity implements AppConfigListener {

    private FragmentPagerAdapter adapterViewPager;
    private MyViewPager vpPager;
    private ArrayList<Page> pages = new ArrayList<>();
    private int Position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_config);

        tinyDB = new TinyDB(ConfigActivity.this);

        if (!tinyDB.getBoolean(AppConfig.FINGER_SHOW) && tinyDB.getBoolean(AppConfig.FINGER)) {
            tinyDB.putBoolean(AppConfig.FINGER_SHOW, true);
            pages.add(new Page(new FingerPrintFragment(this), " Finger Scan Lock ", this.getDrawable(R.drawable.app_lock)));
        }

        if (!tinyDB.getBoolean(AppConfig.IS_PATTERN_SET) && !tinyDB.getBoolean(AppConfig.LOCK_SETTING)) {
            pages.add(new Page(new PatternFragment(this), " Pattern Lock ", this.getDrawable(R.drawable.app_lock)));
        }

        if (!tinyDB.getBoolean(AppConfig.IS_PASSWORD_SET) && !tinyDB.getBoolean(AppConfig.LOCK_SETTING)) {
            pages.add(new Page(new PinFragment(this), " Pin Lock ", this.getDrawable(R.drawable.app_lock)));
        }

        if (tinyDB.getString(AppConfig.ANSWER).isEmpty()) {
            pages.add(new Page(new QuestionFragment(this), " Question ", this.getDrawable(R.drawable.app_lock)));
        }

        vpPager = (MyViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), pages, this);
        vpPager.setAdapter(adapterViewPager);

        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        vpPager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });

        Position = vpPager.getCurrentItem();
    }

    @Override
    public void onSuccess(String Message, int status) {

        Position = vpPager.getCurrentItem();
        Handler handler = new Handler();

        switch (status) {
            case 0:

                if (Position < pages.size() - 1) {
                    Position--;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vpPager.setCurrentItem(Position);
                        }
                    });
                }

                break;
            case 1:

                if (Position < pages.size() - 1) {
                    Position++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vpPager.setCurrentItem(Position);
                        }
                    });

                } else {

                    if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
                        startActivity(PremiumActivity.class, null);
                        finish();
                    } else {
                        startActivity(MainActivity.class, null);
                        finish();
                    }
                }

                break;
            case 2:


                if (Position < pages.size() - 1) {
                    Position = Position + 2;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vpPager.setCurrentItem(Position);
                        }
                    });

                }

                break;
        }

    }

    @Override
    public void onFailed(String Message, int status) {

    }

    @Override
    public void onAskQuestion(String Message, int status) {

        Bundle bundle = new Bundle();
        bundle.putInt(AppConfig.SETTING, status);
        switch (status) {
            case 1:
                startActivity(SettingActivity.class, bundle);
                break;
            case 2:
                startActivity(SettingActivity.class, bundle);
                break;
            default:
        }

    }

    @Override
    public void onSetPassword(String Message, int status) {

    }

    @Override
    public void onPermission(String Message, int Position, PermissionListener permissionListener) {

    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {

        private Context context;
        private Drawable drawable;
        private ArrayList<Page> pages = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Page> pages, Context context) {
            super(fragmentManager);
            this.pages = pages;
            this.context = context;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return pages.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            return pages.get(position).getFragment();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

    }

}