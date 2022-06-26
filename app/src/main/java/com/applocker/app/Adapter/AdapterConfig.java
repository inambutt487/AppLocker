package com.applocker.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.applocker.app.R;

public class AdapterConfig extends PagerAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    int[] layoutsList;

    private ImageView config_icon;
    private TextView config_title;
    private TextView config_desc;


    public AdapterConfig(Context context, int[] layoutsList) {
        this.context = context;
        this.layoutsList = layoutsList;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(layoutsList[position], container, false);
        container.addView(view);

        config_icon = view.findViewById(R.id.config_icon);
        config_title = view.findViewById(R.id.config_title);
        config_desc = view.findViewById(R.id.config_desc);

        switch (position) {
            case 0:
                config_title.setText(context.getResources().getString(R.string.app_scanner));
                break;
            case 1:
                config_title.setText(context.getResources().getString(R.string.app_pin));
                break;
            case 2:
                config_title.setText(context.getResources().getString(R.string.app_pattern));
                break;
            case 3:
                config_title.setText(context.getResources().getString(R.string.app_question));
                break;
            case 4:
                config_title.setText(context.getResources().getString(R.string.app_permission));
                break;
            case 5:
                config_title.setText(context.getResources().getString(R.string.app_icons));
                break;
        }

        return view;
    }

    @Override
    public int getCount() {
        return layoutsList.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
