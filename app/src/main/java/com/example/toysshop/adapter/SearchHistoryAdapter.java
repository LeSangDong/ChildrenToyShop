package com.example.toysshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toysshop.databinding.RowItemSearchBinding;
import com.example.toysshop.listener.IActionSearchListener;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<String> searchHistory;
    private IActionSearchListener listener;

    public SearchHistoryAdapter(List<String> searchHistory, IActionSearchListener listener) {
        this.searchHistory = searchHistory;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemSearchBinding binding = RowItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapter.ViewHolder holder, int position) {
        String query = searchHistory.get(position);
        holder.binding.tvQuery.setText(query);

        holder.binding.iconHistory.setOnClickListener(v->{
            listener.onReturn(query);
        });

        holder.binding.iconClose.setOnClickListener(v->{
            listener.onDeleteById(position);
        });
    }

    @Override
    public int getItemCount() {
        return searchHistory.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemSearchBinding binding;

        public ViewHolder(@NonNull RowItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public void removeItem(int position) {
        searchHistory.remove(position);
        notifyItemRemoved(position);
    }
}
