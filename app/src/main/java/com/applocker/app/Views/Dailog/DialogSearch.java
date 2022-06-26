package com.applocker.app.Views.Dailog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applocker.app.Adapter.MainAdapter;
import com.applocker.app.Interface.LockMainContract;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.R;
import com.applocker.app.MVP.LockMainPresenter;

import java.util.ArrayList;
import java.util.List;

public class DialogSearch extends BaseDialog implements LockMainContract.View {

    private Context mContext;
    private EditText mEditSearch;
    private RecyclerView mRecyclerView;
    private MainAdapter mMainAdapter;
    private LockMainPresenter mLockMainPresenter;

    public DialogSearch(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().gravity = Gravity.TOP;
    }

    @Override
    protected float setWidthScale() {
        return 1;
    }

    @Nullable
    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Nullable
    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        mLockMainPresenter = new LockMainPresenter(this, mContext);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEditSearch = findViewById(R.id.edit_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mMainAdapter = new MainAdapter(mContext);
        mRecyclerView.setAdapter(mMainAdapter);

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(@NonNull Editable editable) {
                if (editable.length() == 0) {
                    mMainAdapter.setLockInfos(new ArrayList<CommLockInfo>());
                } else {
                    mLockMainPresenter.searchAppInfo(editable.toString(), new LockMainPresenter.ISearchResultListener() {
                        @Override
                        public void onSearchResult(List<CommLockInfo> commLockInfos) {
                            mMainAdapter.setLockInfos(commLockInfos);
                        }
                    });
                }
            }
        });

    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_search;
    }


    @Override
    public void loadAppInfoSuccess(List<CommLockInfo> list) {

    }
}

