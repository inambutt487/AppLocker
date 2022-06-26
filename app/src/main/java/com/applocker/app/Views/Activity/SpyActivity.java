package com.applocker.app.Views.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.applocker.app.Adapter.AdapeterSpy;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.R;
import com.applocker.app.Utils.AppLocker;
import com.applocker.app.Utils.BaseActivity;

import java.io.File;
import java.util.ArrayList;

public class SpyActivity extends BaseActivity implements AdapeterSpy.ClickListener {

    private Context context;
    private RecyclerView recyclerView;
    private AdapeterSpy adapterPhotos;
    private ArrayList<String> modelList;
    private ImageView empty;
    private TextView empty_text;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        AppLocker.getInstance().doForCreate(this);
        makeFullScreen();
        setStatusBar(this, R.color.colorPrimary, R.color.white);
        setContentView(R.layout.activity_spy);

        tinyDB = new TinyDB(SpyActivity.this);
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

        context = this;
        modelList = new ArrayList<>();
        modelList = getFilePaths();

        recyclerView = findViewById(R.id.recycler_single_shots);
        empty = (ImageView) findViewById(R.id.empty);
        empty_text = (TextView) findViewById(R.id.empty_text);

        if (modelList.size() > 0) {

            empty.setVisibility(View.GONE);
            empty_text.setVisibility(View.GONE);

            if (modelList.size() >= 3) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                recyclerView.setLayoutManager(gridLayoutManager);
                adapterPhotos = new AdapeterSpy(context, modelList, this);
                adapterPhotos.notifyDataSetChanged();
                recyclerView.setAdapter(adapterPhotos);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(gridLayoutManager);
                adapterPhotos = new AdapeterSpy(context, modelList, this);
                adapterPhotos.notifyDataSetChanged();
                recyclerView.setAdapter(adapterPhotos);
            }


        } else {
            empty.setVisibility(View.VISIBLE);
            empty_text.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<String> getFilePaths() {
        ArrayList<String> filePaths = new ArrayList<String>();

        String root = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name)).toString();
        } else {
            root = android.os.Environment.getExternalStorageDirectory()
                    + File.separator + Environment.DIRECTORY_PICTURES.toString() + "/" + context.getString(R.string.app_name);
        }


        File directory = new File(root);

        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles.length > 0) {

                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {
                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();
                    filePaths.add(filePath);

                }
            } else {
                Log.e("*", "no files");

            }

        } else {

            Log.e("*", "no Dirctory");

        }

        return filePaths;
    }

    @Override
    public void openFile(File file) {

        bundle = new Bundle();
        bundle.putString(AppConfig.IMAGE, file.getAbsolutePath());
        startActivity(ImageActivity.class, bundle);

    }
}
