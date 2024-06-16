package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.databinding.RowViewpager2BannerBinding;

import java.util.List;

public class BannerViewpagerAdapter extends RecyclerView.Adapter<BannerViewpagerAdapter.ViewHolder> {
    List<String> mUrls;
    Context context;

    public BannerViewpagerAdapter(List<String> mUrls) {
        this.mUrls = mUrls;
    }

    @NonNull
    @Override
    public BannerViewpagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowViewpager2BannerBinding binding = RowViewpager2BannerBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewpagerAdapter.ViewHolder holder, int position) {
        String url = mUrls.get(position);
        context = holder.itemView.getContext();
        Glide.with(context).load(url).into(holder.binding.imageView);

    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RowViewpager2BannerBinding binding;

        public ViewHolder(@NonNull RowViewpager2BannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
