package com.example.toysshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.RowItemReviewProductBinding;
import com.example.toysshop.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    List<Review> reviewList;
    Context context;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RowItemReviewProductBinding binding = RowItemReviewProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        context = holder.itemView.getContext();
        Glide.with(context)
                .load(review.getUserImage())
                .placeholder(R.drawable.imag_user_default)
                .into(holder.binding.imageAvatar);
        holder.binding.tvNameUser.setText(review.getUserName());
        holder.binding.tvReview.setText(review.getComment());
        holder.binding.rating.setRating(review.getRating());


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemReviewProductBinding binding;

        public ViewHolder(@NonNull RowItemReviewProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
