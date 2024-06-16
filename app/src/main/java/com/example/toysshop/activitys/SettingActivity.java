package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivitySettingBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.tvLogout.setOnClickListener(v->{
            auth.signOut();
            startActivity(new Intent(SettingActivity.this, GetStartedActivity.class));
            finish();
        });


    }
}