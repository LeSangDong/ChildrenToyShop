package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityOrderSuccessBinding;

public class OrderSuccessActivity extends BaseActivity {
    private ActivityOrderSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnContinue.setOnClickListener(v->{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}