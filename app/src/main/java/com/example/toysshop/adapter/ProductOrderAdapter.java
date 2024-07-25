package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.databinding.RowItemProductOrderBinding;
import com.example.toysshop.model.CartModel;

import java.text.DecimalFormat;
import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ViewHolder> {
    List<CartModel> mList;
    Context context;

    public ProductOrderAdapter(List<CartModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ProductOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemProductOrderBinding binding = RowItemProductOrderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        CartModel cartModel = mList.get(position);
        Glide.with(context).load(cartModel.getImgUrl())
                .into(holder.binding.imageView);
        holder.binding.tvNameProduct.setText(cartModel.getName_product());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPriceProduct.setText(new StringBuilder("Giá: ").append(format.format(cartModel.getPrice())).append("đ"));
        holder.binding.tvQuantity.setText(new StringBuilder("Số lượng: x").append(cartModel.getQuantity()));


    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemProductOrderBinding binding;

        public ViewHolder(@NonNull RowItemProductOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
