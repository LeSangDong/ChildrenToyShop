package com.example.toysshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.SeenImageActivity;
import com.example.toysshop.databinding.RowItemChatBinding;
import com.example.toysshop.model.Message;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;
    private String currentUserId;
    private Context context;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemChatBinding binding = RowItemChatBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        context = holder.itemView.getContext();
        if (message.getUserId().equals(currentUserId)) {
            holder.binding.rightChatLayout.setVisibility(View.VISIBLE);
            holder.binding.leftChatLayout.setVisibility(View.GONE);
            holder.binding.iconFavorite.setVisibility(View.GONE);
            if (message.isImage()) {
                holder.binding.tvMessageRight.setVisibility(View.GONE);
                holder.binding.imageMessageRight.setVisibility(View.VISIBLE);
                Glide.with(holder.binding.imageMessageRight.getContext())
                        .load(message.getMessage())
                        .into(holder.binding.imageMessageRight);
                holder.binding.imageMessageRight.setOnClickListener(v->{
                    Intent intent = new Intent(context, SeenImageActivity.class);
                    intent.putExtra("image_item",message.getMessage());
                    context.startActivity(intent);
                });
            } else {
                holder.binding.tvMessageRight.setVisibility(View.VISIBLE);
                holder.binding.imageMessageRight.setVisibility(View.GONE);
                holder.binding.tvMessageRight.setText(message.getMessage());
            }
            holder.binding.tvTimeRight.setText(message.getTimestamp());

        } else {
            holder.binding.rightChatLayout.setVisibility(View.GONE);
            holder.binding.statusMessage.setVisibility(View.GONE);
            holder.binding.leftChatLayout.setVisibility(View.VISIBLE);
            if (message.isImage()) {
                holder.binding.tvMessageLeft.setVisibility(View.GONE);
                holder.binding.imageMessageLeft.setVisibility(View.VISIBLE);
                Glide.with(holder.binding.imageMessageLeft.getContext())
                        .load(message.getMessage())
                        .into(holder.binding.imageMessageLeft);
                holder.binding.imageMessageLeft.setOnClickListener(v->{
                    Intent intent = new Intent(context, SeenImageActivity.class);
                    intent.putExtra("image_item",message.getMessage());
                    context.startActivity(intent);
                });
            } else {
                holder.binding.tvMessageLeft.setVisibility(View.VISIBLE);
                holder.binding.imageMessageLeft.setVisibility(View.GONE);
                holder.binding.tvMessageLeft.setText(message.getMessage());
            }
            holder.binding.tvTimeLeft.setText(message.getTimestamp());
        }

        holder.binding.iconFavorite.setOnClickListener(v -> {
            message.setFavorite(!message.isFavorite());
            if (message.isFavorite()) {
                holder.binding.iconFavorite.setImageResource(R.drawable.baseline_favorite_red_24);
            } else {
                holder.binding.iconFavorite.setImageResource(R.drawable.baseline_favorite_grey_24);
            }
        });

        // Hiển thị trạng thái yêu thích khi khởi tạo
        if (message.isFavorite()) {
            holder.binding.iconFavorite.setImageResource(R.drawable.baseline_favorite_red_24);
        } else {
            holder.binding.iconFavorite.setImageResource(R.drawable.baseline_favorite_grey_24);
        }



    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemChatBinding binding;

        public ViewHolder(@NonNull RowItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
