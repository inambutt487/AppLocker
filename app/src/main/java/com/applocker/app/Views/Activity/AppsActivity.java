package com.applocker.app.Views.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.applocker.app.BuildConfig;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Interface.LockMainContract;
import com.applocker.app.MVP.LockMainPresenter;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.R;
import com.applocker.app.Service.LockService;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BackgroundManager;
import com.applocker.app.Utils.BaseActivity;
import com.applocker.app.Utils.SystemBarHelper;
import com.applocker.app.Views.Dailog.DialogSearch;
import com.applocker.app.Views.Fragments.AppsFragment;
import com.applocker.app.Views.Fragments.CustomFragment;
import com.applocker.app.Views.Fragments.LockedFragment;
import com.applocker.app.Views.Fragments.UnLockedFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class AppsActivity extends BaseActivity implements LockMainContract.View, View.OnClickListener {


    public static final String TAG = "AppsActivity";

    private static final int RESULT_ACTION_IGNORE_BATTERY_OPTIMIZATION = 351;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private CommentPagerAdapter mPagerAdapter;

    private LockMainPresenter mLockMainPresenter;
    private DialogSearch mDialogSearch;

    private List<String> titles;
    private List<Fragment> fragmentList;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLocker.getInstance().doForCreate(this);
        resetFontScale(getResources().getConfiguration(), this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_apps);

        initializeView();
    }

    private void initializeView() {

        mDialogSearch = new DialogSearch(this);

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
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        });


        title = findViewById(R.id.title);
        mTabLayout = findViewById(R.id.tab_layout);
        wrapTabIndicatorToTitle(mTabLayout, 0, 0);
        mViewPager = findViewById(R.id.view_pager);

        mLockMainPresenter = new LockMainPresenter(this, this);
        mLockMainPresenter.loadAppInfo(this);
    }


    @Override
    public void loadAppInfoSuccess(@NonNull List<CommLockInfo> list) {
        int lockNum = 0;
        int unlockedNum = 0;
        int customelockedNum = 0;


        for (CommLockInfo info : list) {

            if (info.isLocked() && info.isMultilock()) {
                customelockedNum++;
            } else if (info.isLocked() && !info.isMultilock()) {
                lockNum++;
            } else {
                unlockedNum++;
            }

        }

        titles = new ArrayList<>();

        /*titles.add("Apps" + " (" + list.size() + ")");
        titles.add("Unlocked" + " (" + unlockedNum + ")");
        *//*    titles.add("Locked" + " (" + lockNum + ")");*//*
        titles.add("Custom" + " (" + customelockedNum + ")");*/

        titles.add("Lock Apps");
        titles.add("UnLock Apps");
        /*    titles.add("Locked" + " (" + lockNum + ")");*/
        titles.add("Custom Locks");

        AppsFragment appsFragment = AppsFragment.newInstance(list);
        LockedFragment lockedFragment = LockedFragment.newInstance(list);
        /*UnLockedFragment unLockedFragment = UnLockedFragment.newInstance(list);*/
        CustomFragment customFragment = CustomFragment.newInstance(list);

        fragmentList = new ArrayList<>();
        fragmentList.add(appsFragment);
        /*fragmentList.add(unLockedFragment);*/
        fragmentList.add(lockedFragment);
        fragmentList.add(customFragment);
        mPagerAdapter = new CommentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        Bundle bundle = getIntent().getExtras();
        int value = bundle.getInt(AppConfig.SET_APP);
        mViewPager.setCurrentItem(value);

        switch (mViewPager.getCurrentItem()) {
            case 0:
                title.setText("Lock Apps");
                break;
            case 1:
                title.setText("UnLock Apps");
                break;
            case 2:
                title.setText("Custom Locks");
                break;
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        title.setText("Lock Apps");
                        break;
                    case 1:
                        title.setText("UnLock Apps");
                        break;
                    case 2:
                        title.setText("Custom Locks");
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
        }
    }


    public class CommentPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList;
        private List<String> titles;


        public CommentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }
    }

    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }

}