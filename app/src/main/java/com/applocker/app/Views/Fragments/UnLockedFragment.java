package com.applocker.app.Views.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applocker.app.Adapter.MainAdapter;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.R;
import com.applocker.app.Utils.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class UnLockedFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    @Nullable
    private List<CommLockInfo> data, list;
    @Nullable
    private MainAdapter mMainAdapter;

    private TextView header_text;
    private ImageView header_icon;

    @NonNull
    public static UnLockedFragment newInstance(List<CommLockInfo> list) {
        UnLockedFragment unLockedFragment = new UnLockedFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", (ArrayList<? extends Parcelable>) list);
        unLockedFragment.setArguments(bundle);
        return unLockedFragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init(View rootView) {
        header_text = (TextView) findViewById(R.id.header_text);
        header_text.setText("Touch the app icon to Lock it. ");
        header_icon = (ImageView) findViewById(R.id.header_icon);
        header_icon.setBackgroundResource(R.drawable.lock_app_icon);
        header_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = getArguments().getParcelableArrayList("data");
        mMainAdapter = new MainAdapter(getContext());
        mRecyclerView.setAdapter(mMainAdapter);
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

        mMainAdapter.setLockInfos(list);
    }

    @Override
    public void onStop() {
        super.onStop();
        list.clear();
    }
}
