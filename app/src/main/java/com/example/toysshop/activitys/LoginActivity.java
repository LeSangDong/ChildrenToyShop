package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.accounts.SharedPrefManager;
import com.example.toysshop.databinding.ActivityLoginBinding;
import com.example.toysshop.databinding.ActivityMainBinding;
import com.example.toysshop.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iNit();
        loginAccount();


    }

    private void loginAccount() {
        binding.btnLogin.setOnClickListener(v->{
            binding.progressBar.setVisibility(View.VISIBLE);
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPass.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                binding.progressBar.setVisibility(View.GONE);
                binding.edtEmail.setError("Vui lòng nhập email");
                return;
            }
            if(TextUtils.isEmpty(password)){
                binding.progressBar.setVisibility(View.GONE);
                binding.edtPass.setError("Vui lòng nhập mật khẩu");
                return;
            }

            User user = new User(email, password);
            auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference("account")
                                        .child(currentUser.getUid());
                                loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String role = snapshot.child("role").getValue(String.class);
                                            if (role != null) {
                                                // Lưu thông tin vào SharedPreferences
                                                String name = snapshot.child("info").child("name").getValue(String.class);
                                                SharedPrefManager.getInstance(LoginActivity.this).saveUser(email, name, role);

                                                if (role.equals("admin")) {
                                                    startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
                                                } else {
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                }
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        Snackbar.make(binding.layoutBottomLogin, "Đăng nhập thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Snackbar.make(binding.layoutBottomLogin, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
        });
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
}