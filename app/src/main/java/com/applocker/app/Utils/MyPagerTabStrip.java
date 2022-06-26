package com.applocker.app.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.PagerTabStrip;

public class MyPagerTabStrip extends PagerTabStrip {
    private boolean isTabSwitchEnabled;

    public MyPagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isTabSwitchEnabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isTabSwitchEnabled) {
            return super.onInterceptTouchEvent(event);
        } else {
            return true;
        }
    }

    public void setTabSwitchEnabled(boolean isSwipeEnabled) {
        this.isTabSwitchEnabled = isSwipeEnabled;
    }
}
