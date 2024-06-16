package com.example.toysshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toysshop.databinding.RowItemImageBinding;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<Uri> images;
    Context context;

    public ImageAdapter(ArrayList<Uri> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemImageBinding binding = RowItemImageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        Uri imageUri = images.get(position);
        holder.binding.imageView.setImageURI(imageUri);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemImageBinding binding;

        public ViewHolder(@NonNull RowItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
