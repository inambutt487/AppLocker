package com.applocker.app.Utils.Pin.util;

import android.annotation.TargetApi;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by Arcane on 7/23/2017.
 */

public class Animate {

    @TargetApi(Build.VERSION_CODES.M)
    public static void animate(AppCompatImageView view, AnimatedVectorDrawable scanFingerprint) {
        view.setImageDrawable(scanFingerprint);
        scanFingerprint.start();
    }
}
