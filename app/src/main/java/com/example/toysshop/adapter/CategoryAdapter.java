package com.example.toysshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.ShowProductActivity;
import com.example.toysshop.databinding.RowItemCategoryBinding;
import com.example.toysshop.listener.ICategoryItemClickListener;
import com.example.toysshop.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<Category> mCategorys;
    Context context;

    public CategoryAdapter(List<Category> mCategorys) {
        this.mCategorys = mCategorys;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemCategoryBinding binding = RowItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = mCategorys.get(position);
        context = holder.itemView.getContext();
        holder.binding.tvTitle.setText(category.getName());
        Glide.with(context).load(category.getImagePath()).into(holder.binding.imageView);

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, ShowProductActivity.class);
            intent.putExtra("category_id",category.getId());
            intent.putExtra("category_name",category.getName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mCategorys.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemCategoryBinding binding;

        public ViewHolder(@NonNull RowItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
