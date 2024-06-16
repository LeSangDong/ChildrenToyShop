package com.example.toysshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.activitys.SeenImageActivity;
import com.example.toysshop.databinding.RowItemViewpagerBinding;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    ArrayList<String> mListImage;
    Context context;

    public ViewPagerAdapter(ArrayList<String> mListImage) {
        this.mListImage = mListImage;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemViewpagerBinding binding = RowItemViewpagerBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {

        String image = mListImage.get(position);
        context = holder.itemView.getContext();
        Glide.with(context).load(image).into(holder.binding.imageView);

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, SeenImageActivity.class);
            intent.putExtra("image_item",image);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return mListImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemViewpagerBinding binding;

        public ViewHolder(@NonNull RowItemViewpagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
