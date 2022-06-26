package com.applocker.app.Model;

import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

public class Page {

    private Fragment fragment;
    private String title;
    private Drawable icon;

    public Page(Fragment fragment, String title, Drawable icon) {
        this.fragment = fragment;
        this.title = title;
        this.icon = icon;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
