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
import com.example.toysshop.activitys.DetailProductActivity;
import com.example.toysshop.databinding.RowItemBestToyBinding;
import com.example.toysshop.model.Toy;

import java.text.DecimalFormat;
import java.util.List;

public class ToyAdapter extends RecyclerView.Adapter<ToyAdapter.ViewHolder> {

    List<Toy> mListToy;
    Context context;

    public ToyAdapter(List<Toy> mListToy) {
        this.mListToy = mListToy;
    }

    @NonNull
    @Override
    public ToyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemBestToyBinding binding = RowItemBestToyBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ToyAdapter.ViewHolder holder, int position) {

        Toy toy = mListToy.get(position);
        context = holder.itemView.getContext();
        Glide.with(context)
                .load(toy.getImageList().get(0))
                .into(holder.binding.imageView);

        holder.binding.tvNameToy.setText(toy.getTitle());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPrice.setText(new StringBuilder().append(format.format(toy.getPrice())).append("đ"));
        holder.binding.ratingbar.setRating((float) toy.getStar());
        if(toy.getPriceDiscount() >0){
            holder.binding.tvPercent.setText(new StringBuilder("-").append(toy.getPriceDiscount()).append("%"));
        }
        else{
            holder.binding.tvPercent.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("toy", toy); // Truyền đối tượng Toys qua Intent
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        });




    }

    @Override
    public int getItemCount() {
        return mListToy.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemBestToyBinding binding;

        public ViewHolder(@NonNull RowItemBestToyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
