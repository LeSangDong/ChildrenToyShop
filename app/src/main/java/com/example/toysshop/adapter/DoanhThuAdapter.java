package com.example.toysshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toysshop.databinding.RowItemDoanhthuBinding;
import com.example.toysshop.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class DoanhThuAdapter extends RecyclerView.Adapter<DoanhThuAdapter.ViewHolder> {
    List<Order> orderedList;

    public DoanhThuAdapter(List<Order> orderedList) {
        this.orderedList = orderedList;
    }

    @NonNull
    @Override
    public DoanhThuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemDoanhthuBinding binding = RowItemDoanhthuBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DoanhThuAdapter.ViewHolder holder, int position) {
        Order ordered = orderedList.get(position);
        holder.binding.tvOrderid.setText(ordered.getOrderId());
        holder.binding.tvDay.setText(ordered.getOrderDate());
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvSum.setText(new StringBuilder().append(format.format(ordered.getTotalPrice())).append(" VND"));

    }

    @Override
    public int getItemCount() {
        return orderedList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemDoanhthuBinding binding;

        public ViewHolder(@NonNull RowItemDoanhthuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
