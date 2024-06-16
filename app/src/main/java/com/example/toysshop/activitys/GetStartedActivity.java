package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityGetStartedBinding;

public class GetStartedActivity extends BaseActivity {
    private ActivityGetStartedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionButtons();
    }

    private void actionButtons() {
        binding.btnSignup.setOnClickListener(v->{
            startActivity(new Intent(GetStartedActivity.this,SignUpUserActivity.class));
        });
        binding.btnLogin.setOnClickListener(v->{
            startActivity(new Intent(GetStartedActivity.this,LoginActivity.class));

        });
    }
}