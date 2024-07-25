package com.example.toysshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.icu.text.CaseMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toysshop.databinding.RowItemNotificationBinding;
import com.example.toysshop.model.Order;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    List<Order> mListOrder;
    Context context;
    private DatabaseReference ordersRef;

    public NotificationAdapter(List<Order> mListOrder) {
        this.mListOrder = mListOrder;
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemNotificationBinding binding = RowItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Order order = mListOrder.get(position);

        if("đã bị từ chối".equals(order.getStatus())){
            String productName = "";
            if (!order.getCartItems().isEmpty()) {
                productName = order.getCartItems().get(0).getName_product();
            }
            String message = String.format("Đơn hàng (%s) của bạn đã bị từ chối vì lý do %s.",
                    productName, order.getMessage_reason().toLowerCase());
            holder.binding.tvMessage.setText(message);

            holder.binding.tvTimeNotification.setText(new StringBuilder().append(order.getTime_deny()));
        }
        if("đang vận chuyển".equals(order.getStatus())){
            String productName = "";
            if (!order.getCartItems().isEmpty()) {
                productName = order.getCartItems().get(0).getName_product();
            }
            String message = String.format("Đơn hàng (%s) của bạn đã được xác nhận.",
                    productName);
            holder.binding.tvMessage.setText(message);

            holder.binding.tvTimeNotification.setText(new StringBuilder().append(order.getTime_deny()));
        }



    }

    @Override
    public int getItemCount() {
        return mListOrder.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
        RowItemNotificationBinding binding;

        public ViewHolder(@NonNull RowItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
