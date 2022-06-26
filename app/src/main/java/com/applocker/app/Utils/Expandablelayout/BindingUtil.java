package com.applocker.app.Utils.Expandablelayout;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

/**
 * Created by Fawzy on 02,March,2019.
 * ma7madfawzy@gmail.com
 */

public class BindingUtil {
    @BindingAdapter({"exp_src"})
    public static void setImageBackground(ImageView view, Drawable drawable) {
        if (drawable == null)
            return;
        view.setBackground(null);
        view.setBackgroundResource(0);
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("exp_visibility")
    public static void visibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("exp_fontPath")
    public static void setFont(TextView view, String fontPath) {
        if (fontPath != null) {
            try {
                Typeface type = Typeface.createFromAsset(view.getContext().getAssets(), fontPath);
                view.setTypeface(type, view.getTypeface().getStyle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @BindingAdapter({"exp_content"})
    public static void setContent(ExpandableLayout view, String content_text) {
        view.setContent(content_text);
    }

    @BindingAdapter({"exp_title"})
    public static void setTitle(ExpandableLayout view, String content_text) {
        view.setTitle(content_text);
    }

}
