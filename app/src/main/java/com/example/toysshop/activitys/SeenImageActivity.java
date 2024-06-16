package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivitySeenImageBinding;

public class SeenImageActivity extends AppCompatActivity {
    private ActivitySeenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String image = getIntent().getStringExtra("image_item");

        Glide.with(SeenImageActivity.this)
                .load(image).into(binding.imageView);
    }
}