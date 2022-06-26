package com.applocker.app.Adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applocker.app.Database.CommLockInfoManager;
import com.applocker.app.Model.CommLockInfo;
import com.applocker.app.R;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MainViewHolder> {

    @NonNull
    private List<CommLockInfo> mLockInfos = new ArrayList<>();

    private PackageManager packageManager;
    private CommLockInfoManager mLockInfoManager;

    private onClick onclick = null;

    public CustomAdapter(Context mContext, onClick onclick) {
        // this.mContext = mContext;
        this.onclick = onclick;
        packageManager = mContext.getPackageManager();
        mLockInfoManager = new CommLockInfoManager(mContext);
    }

    public void setLockInfos(@NonNull List<CommLockInfo> lockInfos) {
        mLockInfos.clear();
        mLockInfos.addAll(lockInfos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_custome_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        final CommLockInfo lockInfo = mLockInfos.get(position);
        initData(holder.mAppName, holder.mSwitchCompat, holder.mAppIcon, lockInfo);
        holder.mSwitchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(lockInfo.isLocked() && lockInfo.isMultilock()){
                    changeItemLockStatus(holder.mSwitchCompat, lockInfo, position);
                }else{
                    onclick.CustomLock(lockInfo);
                }
                holder.mSwitchCompat.setChecked(false);
            }
        });
    }


    private void initData(TextView tvAppName, CheckBox switchCompat, ImageView mAppIcon, CommLockInfo lockInfo) {
        tvAppName.setText(packageManager.getApplicationLabel(lockInfo.getAppInfo()));
        switchCompat.setChecked(lockInfo.isLocked() && lockInfo.isMultilock());
        ApplicationInfo appInfo = lockInfo.getAppInfo();
        mAppIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo));
    }

    public void changeItemLockStatus(@NonNull CheckBox checkBox, @NonNull CommLockInfo info, int position) {
        info.setLocked(false);
        info.setMultilock(false);
        mLockInfoManager.unlockCustomApplication(info.getPackageName());
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mLockInfos.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAppIcon;
        private TextView mAppName;
        private CheckBox mSwitchCompat;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            mAppIcon = itemView.findViewById(R.id.app_icon);
            mAppName = itemView.findViewById(R.id.app_name);
            mSwitchCompat = itemView.findViewById(R.id.switch_compat);
        }
    }

    public interface onClick{
        void CustomLock(CommLockInfo commLockInfo);
    }
}
