package com.example.toysshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.FeedbackActivity;
import com.example.toysshop.databinding.RowItemProductFeedbackBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class ProductFeedBackAdapter extends RecyclerView.Adapter<ProductFeedBackAdapter.ViewHolder> {
    List<CartModel> mList;
    Context context;

    public ProductFeedBackAdapter(List<CartModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ProductFeedBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      RowItemProductFeedbackBinding binding = RowItemProductFeedbackBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductFeedBackAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        CartModel cartModel = mList.get(position);
        Glide.with(context)
                .load(cartModel.getImgUrl())
                .placeholder(R.drawable.noimage).into(holder.binding.imageProduct);
        holder.binding.tvNameProduct.setText(cartModel.getName_product());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPriceProduct.setText(new StringBuilder("Giá: ").append(format.format(cartModel.getPrice())).append(" đ"));

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, FeedbackActivity.class);
            intent.putExtra("name",cartModel.getName_product());
            intent.putExtra("product_id",cartModel.getProductId());
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemProductFeedbackBinding binding;

        public ViewHolder(@NonNull RowItemProductFeedbackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
