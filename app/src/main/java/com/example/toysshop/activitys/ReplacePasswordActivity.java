package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityReplacePasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReplacePasswordActivity extends AppCompatActivity {
    private ActivityReplacePasswordBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReplacePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iNit();

        binding.btnReplace.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
            changePassword();
        });
        binding.btnBack.setOnClickListener(v->{
            finish();
        });
    }

    private void changePassword() {
        String oldPass = binding.edtOldPass.getText().toString().trim();
        String newPass = binding.edtNewPass.getText().toString().trim();
        String confirmNewPass = binding.edtConfirmNewPass.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            binding.progress.setVisibility(View.GONE);
            binding.tvBtn.setVisibility(View.VISIBLE);
            binding.edtOldPass.setError("Nhập mật khẩu cũ");
            return;
        }
        if (TextUtils.isEmpty(newPass)) {
            binding.progress.setVisibility(View.GONE);
            binding.tvBtn.setVisibility(View.VISIBLE);
            binding.edtOldPass.setError("Nhập mật khẩu mới");
            return;
        }
        if (TextUtils.isEmpty(confirmNewPass)) {
            binding.progress.setVisibility(View.GONE);
            binding.tvBtn.setVisibility(View.VISIBLE);
            binding.edtConfirmNewPass.setError("Xác nhận mật khẩu mới");
            return;
        }
        if (!newPass.equals(confirmNewPass)) {
            binding.progress.setVisibility(View.GONE);
            binding.tvBtn.setVisibility(View.VISIBLE);
            binding.edtConfirmNewPass.setError("Mật khẩu mới không khớp");
            return;
        }
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            binding.progress.setVisibility(View.GONE);
                                            binding.tvBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(ReplacePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        } else {
                                            binding.progress.setVisibility(View.GONE);
                                            binding.tvBtn.setVisibility(View.VISIBLE);
                                            Toast.makeText(ReplacePasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            binding.progress.setVisibility(View.GONE);
                            binding.tvBtn.setVisibility(View.VISIBLE);
                            binding.edtOldPass.setError("Mật khẩu cũ không đúng");
                        }
                    });
        }
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
}