package com.example.toysshop.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.RowItemProductAdminBinding;
import com.example.toysshop.model.Toy;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ViewHolder> {
    List<Toy> mListToy;
    Context context;

    public ProductAdminAdapter(List<Toy> mListToy) {
        this.mListToy = mListToy;
    }

    @NonNull
    @Override
    public ProductAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemProductAdminBinding binding = RowItemProductAdminBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdminAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        Toy toy = mListToy.get(position);
        Glide.with(context)
                .load(toy.getImageList().get(0))
                .placeholder(R.drawable.noimage)
                .into(holder.binding.imageView);
        holder.binding.tvNameProduct.setText(new StringBuilder().append(toy.getTitle()));
        DecimalFormat format = new DecimalFormat("#,###");
        if (toy.getPriceDiscount() > 0) {
            double discountedPrice = toy.getPrice() * (1 - toy.getPriceDiscount() / 100.0);
            holder.binding.tvPriceOld.setPaintFlags(  holder.binding.tvPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.tvPriceOld.setText(new StringBuilder("Giá: ").append(format.format(toy.getPrice())).append("đ"));

            holder.binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(discountedPrice)).append("đ"));
        }
        else{
            holder.binding.tvPriceOld.setVisibility(View.GONE);
            holder.binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(toy.getPrice())).append("đ"));
        }
        holder.binding.tvDescription.setText(toy.getDescription());
       if(toy.isBestToy()){
           holder.binding.tvStatus.setText(new StringBuilder().append("Phổ biến"));
       }
       else{
           holder.binding.labelsStatus.setVisibility(View.GONE);
           holder.binding.tvStatus.setVisibility(View.GONE);
       }

    }

    @Override
    public int getItemCount() {
        return mListToy.size();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder{
        RowItemProductAdminBinding binding;

        public ViewHolder(@NonNull RowItemProductAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
