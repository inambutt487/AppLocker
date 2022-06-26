package com.applocker.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.applocker.app.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapeterSpy extends RecyclerView.Adapter<AdapeterSpy.MyViewHolder> {

    private List<String> list;
    Context context;
    private ClickListener clickListener;

    public AdapeterSpy(Context context, List<String> ModelArrayList, ClickListener clickListener) {
        this.context = context;
        list = new ArrayList<>();
        list = ModelArrayList;
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Glide.with(context).load("file://" + list.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_layout, viewGroup, false);

        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_save);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int item_position = getAdapterPosition();

            File file = new File(list.get(item_position));

            clickListener.openFile(file);
        }


    }


    public interface ClickListener {
        void openFile(File file);
    }
}

