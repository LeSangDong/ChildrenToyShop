package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.RowItemImageBinding;

import java.util.List;

public class ImageFirebaseAdapter extends RecyclerView.Adapter<ImageFirebaseAdapter.ViewHolder> {
    List<String> imageList;

    Context context;

    public ImageFirebaseAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageFirebaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemImageBinding binding = RowItemImageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFirebaseAdapter.ViewHolder holder, int position) {
        String image = imageList.get(position);
        context = holder.itemView.getContext();
        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.noimage)
                .into(holder.binding.imageView);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemImageBinding binding;

        public ViewHolder(@NonNull RowItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
