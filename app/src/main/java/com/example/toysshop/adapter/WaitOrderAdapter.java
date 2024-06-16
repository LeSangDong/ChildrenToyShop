package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.RowItemWaitOrderBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class WaitOrderAdapter extends RecyclerView.Adapter<WaitOrderAdapter.ViewHolder> {
    List<CartModel> mList;
    Context context;

    public WaitOrderAdapter(List<CartModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public WaitOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemWaitOrderBinding binding = RowItemWaitOrderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitOrderAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        CartModel cartItem = mList.get(position);
        holder.binding.tvStatus.setText(new StringBuilder().append("Chờ xác nhận"));
        holder.binding.tvNameProduct.setText(cartItem.getName_product());
        holder.binding.tvQuantity.setText(new StringBuilder("Số lượng: ").append(cartItem.getQuantity()));
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvTotalprice.setText(new StringBuilder().append(" | ").append(format.format(cartItem.getPrice()*cartItem.getQuantity()+22000)).append("đ"));
        Glide.with(context)
                .load(cartItem.getImgUrl())
                .placeholder(R.drawable.noimage)
                .into(holder.binding.imageProduct);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemWaitOrderBinding binding;

        public ViewHolder(@NonNull RowItemWaitOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
