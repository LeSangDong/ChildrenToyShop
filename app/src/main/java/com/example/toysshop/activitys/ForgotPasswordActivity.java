package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v->{
            finish();
        });
        binding.btnConfirm.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progressLoading.setVisibility(View.VISIBLE);

            String email = binding.edtEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.progressLoading.setVisibility(View.GONE);
                Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        binding.tvBtn.setVisibility(View.VISIBLE);
                        binding.progressLoading.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Chúng tôi đã gửi thông báo reset email, vui lòng kiểm tra email của bạn.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    });

        });


    }
}