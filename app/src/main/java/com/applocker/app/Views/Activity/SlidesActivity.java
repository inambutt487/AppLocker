package com.applocker.app.Views.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.applocker.app.Adapter.AdapterIntroSlidesViewPager;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;

import java.util.concurrent.Executor;

public class SlidesActivity extends BaseActivity {


    private PreferenceManager preferenceManager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] slides;
    private Button btnSkip, btnNext;
    private ViewPager viewPager;
    private AdapterIntroSlidesViewPager pagerAdapter;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_slides);

        context = this;
        initViews();
        setUpViewPager();
        addBottomDots(0);
    }


    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.acIntroSlide_viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.acIntroSlides_dots);
        btnSkip = (Button) findViewById(R.id.acIntroSlides_btnSkip);
        btnNext = (Button) findViewById(R.id.acIntroSlides_btnNext);
        viewPager.setOnPageChangeListener(viewPagerPageChangeListener);
        btnNext.setOnClickListener(onButtonsClicked);
        btnSkip.setOnClickListener(onButtonsClicked);
    }

    private void setUpViewPager() {
        slides = new int[]{
                R.layout.layout_introslide_one,
                R.layout.layout_introslide_two,
                R.layout.layout_introslide_three
        };
        pagerAdapter = new AdapterIntroSlidesViewPager(this, slides);
        viewPager.setAdapter(pagerAdapter);
    }

    private void launchPrivacyPolicyScreen(boolean isFirstLaunch) {

        tinyDB.putBoolean(AppConfig.FIRST_TIME_APP_INSTALL, isFirstLaunch);

        // Create an executor that executes tasks in the main thread.
        Executor mainExecutor = ContextCompat.getMainExecutor(context);

        // Execute a task in the main thread
        mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // You code logic goes here.
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[slides.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(40);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    View.OnClickListener onButtonsClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.acIntroSlides_btnNext: {
                    int current = getItem(+1);
                    if (current < slides.length) {
                        viewPager.setCurrentItem(current);
                    } else {
                        launchPrivacyPolicyScreen(true);
                    }

                }
                break;
                case R.id.acIntroSlides_btnSkip: {
                    launchPrivacyPolicyScreen(false);
                }
                break;
            }
        }
    };

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == slides.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.INVISIBLE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

}
