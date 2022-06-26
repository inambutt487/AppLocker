package com.applocker.app.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Utils.BaseActivity;
import com.bumptech.glide.Glide;

public class ImageActivity extends BaseActivity {


    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        tinyDB = new TinyDB(ImageActivity.this);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!tinyDB.getBoolean(AppConfig.PURCHASE)) {
            RelativeLayout main_setting = findViewById(R.id.main_setting);
            main_setting.setVisibility(View.VISIBLE);
            main_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConfig.IN_APP, true);
                    startActivity(PremiumActivity.class, bundle);
                }
            });
        }

        imageView = findViewById(R.id.imageView);
        Bundle bundle = getIntent().getExtras();
        String value = bundle.getString(AppConfig.IMAGE);
        Glide.with(ImageActivity.this).load(value).into(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}