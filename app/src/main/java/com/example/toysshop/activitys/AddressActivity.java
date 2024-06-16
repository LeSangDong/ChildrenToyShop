package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityAddressBinding;

public class AddressActivity extends AppCompatActivity {
    private ActivityAddressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("address")) {
            String address = intent.getStringExtra("address");
            binding.tvAddress.setText(address);
        }


    }
}