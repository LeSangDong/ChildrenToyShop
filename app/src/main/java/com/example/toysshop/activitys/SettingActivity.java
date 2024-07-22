package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivitySettingBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // Ensure you have this line
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnSignOut.setOnClickListener(v -> {
            signOut();
        });

        binding.itemInfoPerson.setOnClickListener(v->{
            startActivity(new Intent(this,ViewInfoActivity.class));
        });
        binding.itemReplacePass.setOnClickListener(v->{
            startActivity(new Intent(this,ReplacePasswordActivity.class));
        });
    }

    private void signOut() {
        // Đăng xuất khỏi Firebase Authentication
        auth.signOut();

        // Đăng xuất và xóa tài khoản khỏi Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                googleSignInClient.revokeAccess().addOnCompleteListener(SettingActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Chuyển hướng người dùng đến màn hình đăng nhập với Google mới
                        startActivity(new Intent(SettingActivity.this, GetStartedActivity.class));
                        finish(); // Kết thúc SettingActivity sau khi đăng xuất
                    }
                });
            }
        });
    }
}
