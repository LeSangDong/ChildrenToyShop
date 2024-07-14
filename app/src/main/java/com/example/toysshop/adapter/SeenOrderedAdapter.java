package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.toysshop.R;
import com.example.toysshop.databinding.RowItemSeenOrderedBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class SeenOrderedAdapter extends RecyclerView.Adapter<SeenOrderedAdapter.ViewHolder> {
    List<CartModel> mList;
    Context context;

    public SeenOrderedAdapter(List<CartModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public SeenOrderedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemSeenOrderedBinding binding = RowItemSeenOrderedBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeenOrderedAdapter.ViewHolder holder, int position) {

        context = holder.itemView.getContext();
        CartModel cartItem = mList.get(position);
        holder.binding.tvNameProduct.setText(cartItem.getName_product());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPrice.setText(new StringBuilder("Giá: ").append(format.format(cartItem.getPrice())).append("đ"));
        holder.binding.tvQuantity.setText(new StringBuilder("Số lượng: ").append(cartItem.getQuantity()));
        holder.binding.tvTotalPrice.setText(new StringBuilder("Tổng tiền: ").append(format.format(cartItem.getTotalPrice()*cartItem.getQuantity())).append("đ"));
        Glide.with(context)
                .load(cartItem.getImgUrl())
                .placeholder(R.drawable.noimage)
                .transform(new CenterCrop())
                .into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder{
        RowItemSeenOrderedBinding binding;

        public ViewHolder(@NonNull RowItemSeenOrderedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
