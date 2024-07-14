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
import com.example.toysshop.activitys.ChatActivity;
import com.example.toysshop.databinding.RowItemUserBinding;
import com.example.toysshop.model.User;
import com.example.toysshop.model.UserChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    List<UserChat> userList;
    Context context;

    public UserChatAdapter(List<UserChat> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemUserBinding binding = RowItemUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        UserChat user = userList.get(position);
        Glide.with(context)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.imag_user_default)
                .into(holder.binding.imageAvatar);
        holder.binding.tvNameUser.setText(new StringBuilder().append(user.getName()));
        holder.binding.tvMessageLast.setText(user.getLastMessage());
        holder.binding.tvTime.setText(formatLastMessageTime(user.getLastMessageTime()));

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatWithUserId", user.getUserId());
            intent.putExtra("user_name",user.getName());
            intent.putExtra("url",user.getAvatarUrl());
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemUserBinding binding;

        public ViewHolder(@NonNull RowItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private String formatLastMessageTime(String lastMessageTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        try {
            Date messageDate = sdf.parse(lastMessageTime);
            Date now = new Date();
            long diffInMillis = now.getTime() - messageDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (diffInDays == 0) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                return timeFormat.format(messageDate);
            } else {
                return diffInDays + " ngày trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return lastMessageTime;
        }
    }
}
