package com.example.toysshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.SeenDetailOrderedActivity;
import com.example.toysshop.databinding.RowItemWaitOrderBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WaitOrderAdapter extends RecyclerView.Adapter<WaitOrderAdapter.ViewHolder> {
    List<Order> mList;
    Context context;

    public WaitOrderAdapter(List<Order> mList) {
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
        Order order  = mList.get(position);
        holder.binding.tvStatus.setText(new StringBuilder().append(order.getStatus()));
        holder.binding.tvOrderDate.setText(new StringBuilder("Ngày đặt hàng: ").append(order.getOrderDate()));
       if(order.getCartItems() != null && !order.getCartItems().isEmpty()){
           List<CartModel> cartItems = order.getCartItems();
           StringBuilder productNames = new StringBuilder();
           for (int i = 0; i < cartItems.size(); i++) {
               productNames.append(cartItems.get(i).getName_product());
               if (i < cartItems.size() - 1) {
                   productNames.append(", ");
               }
           }
           holder.binding.tvNameProduct.setText(productNames.toString());
           int totalQuantity = 0;
           for (CartModel item : cartItems) {
               totalQuantity += item.getQuantity();
           }
           holder.binding.tvQuantity.setText(new StringBuilder().append("Số lượng: ").append(totalQuantity));
           int totalPrice = 0;
           for (CartModel item : cartItems) {
               totalPrice += item.getPrice() * item.getQuantity();
           }
           DecimalFormat format = new DecimalFormat("#,###");
           holder.binding.tvTotalprice.setText(new StringBuilder("| ").append("Tổng tiền: ").append(format.format(totalPrice)).append("đ"));
           Glide.with(context)
                   .load(cartItems.get(0).getImgUrl())
                   .placeholder(R.drawable.noimage)
                   .into(holder.binding.imageView);
       }
       holder.binding.btnSeen.setOnClickListener(v->{
           Intent intent = new Intent(context, SeenDetailOrderedActivity.class);
           intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(order.getCartItems()));
           context.startActivity(intent);
       });
//       holder.binding.point1.setBackgroundColor(ContextCompat.getColor(context, R.color.sky));
//       holder.binding.point2.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_icon));
       if(order.getStatus().equals("Chờ xác nhận")){
           holder.binding.tvStatusDelivery.setVisibility(View.GONE);
         holder.binding.point2None.setVisibility(View.VISIBLE);
         holder.binding.point2.setVisibility(View.GONE);
         holder.binding.point1None.setVisibility(View.GONE);
       }
       else{
           holder.binding.tvStatus.setVisibility(View.GONE);
         holder.binding.point1.setVisibility(View.GONE);
         holder.binding.point2.setVisibility(View.VISIBLE);
         holder.binding.point2None.setVisibility(View.GONE);
       }
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
