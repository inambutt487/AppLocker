package com.applocker.app.Views.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applocker.app.Adapter.CustomAdapter;
import com.applocker.app.Config.AppConfig;
import com.applocker.app.Database.TinyDB;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.R;
import com.applocker.app.Utils.BaseFragment;
import com.applocker.app.Views.Activity.CustomActivity;
import com.applocker.app.Views.Activity.SettingActivity;
import com.applocker.app.Views.Dailog.LockDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CustomFragment extends BaseFragment implements CustomAdapter.onClick, LockDialog.LockedType {

    public static final String TAG = "CustomFragment";

    private RecyclerView mRecyclerView;
    @Nullable
    private List<CommLockInfo> data, list;
    @Nullable
    private CustomAdapter customAdapter;

    private TinyDB tinyDB = null;

    private TextView header_text;
    private ImageView header_icon;

    @NonNull
    public static CustomFragment newInstance(List<CommLockInfo> list) {
        CustomFragment appsFragment = new CustomFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) list);
        appsFragment.setArguments(bundle);
        return appsFragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init(View rootView) {
        tinyDB = new TinyDB(getContext());

        header_text = (TextView) findViewById(R.id.header_text);
        header_text.setText("If you add a new password,\n" +
                "you can set a different password \n" +
                "for each locked app.\n");
        header_icon = (ImageView) findViewById(R.id.header_icon);
        header_icon.setBackgroundResource(R.drawable.custom_lock_icon);
        header_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = getArguments().getParcelableArrayList("data");
        customAdapter = new CustomAdapter(getContext(), this);
        mRecyclerView.setAdapter(customAdapter);
        list = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        for (CommLockInfo info : data) {
            list.add(info);
        }

        Collections.sort(list, new Comparator<CommLockInfo>(){
            public int compare(CommLockInfo obj1, CommLockInfo obj2) {
                // ## Ascending order
                return obj1.getAppName().compareToIgnoreCase(obj2.getAppName()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });

        customAdapter.setLockInfos(list);
    }

    @Override
    public void onStop() {
        super.onStop();
        list.clear();
    }

    @Override
    public void CustomLock(CommLockInfo commLockInfo) {
        LockDialog dialog = new LockDialog(getActivity(), commLockInfo.getAppName(), this, commLockInfo.getPackageName());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void lock(int type, String app_package) {

        Bundle bundle = new Bundle();
        ;
        bundle.putInt(AppConfig.SET_CUSTOME, type);
        bundle.putString(AppConfig.CUSTOM_APP, app_package);

        switch (type) {
            case 1:

            case 2:
                startActivity(new Intent(getContext(), CustomActivity.class).putExtras(bundle));
                getActivity().finish();
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        customAdapter.notifyDataSetChanged();
    }
}
