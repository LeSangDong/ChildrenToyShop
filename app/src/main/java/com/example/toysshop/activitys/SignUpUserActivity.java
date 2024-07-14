package com.example.toysshop.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.accounts.SharedPrefManager;
import com.example.toysshop.databinding.ActivitySignUpUserBinding;
import com.example.toysshop.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpUserActivity extends BaseActivity {
    private ActivitySignUpUserBinding binding;
    private FirebaseAuth auth;
    private String role;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        signupAccountUser();
        actionLogin();



    }




    private void actionLogin() {
        binding.btnLogin.setOnClickListener(v->{
            startActivity(new Intent(SignUpUserActivity.this,LoginActivity.class));
            finish();
        });
    }

    private void signupAccountUser() {
        binding.btnSignup.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            String firstName = binding.edtFirstName.getText().toString().trim();
            String lastName = binding.edtLastName.getText().toString().trim();
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPass.getText().toString().trim();

            if(TextUtils.isEmpty(firstName)){
                binding.progressBar.setVisibility(View.GONE);
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.edtFirstName.setError("Vui lòng nhập họ");
                return;
            }
            if(TextUtils.isEmpty(lastName)){
                binding.progressBar.setVisibility(View.GONE);
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.edtLastName.setError("Vui lòng nhập tên");
                return;
            }
            if(TextUtils.isEmpty(email)){
                binding.progressBar.setVisibility(View.GONE);
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.edtEmail.setError("Vui lòng nhập email");
                return;
            }
            if(TextUtils.isEmpty(password)){
                binding.progressBar.setVisibility(View.GONE);
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.edtPass.setError("Vui lòng nhập mật khẩu");
                return;
            }

            User user = new User(firstName, lastName, email, password);
            auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account")
                                        .child(currentUser.getUid());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("name", firstName + " " + lastName);
                                userRef.child("info").setValue(hashMap);


                                if (binding.checkSignupAdmin.isChecked()) {
                                    userRef.child("role").setValue("admin");
                                    role = "admin";
                                    startActivity(new Intent(SignUpUserActivity.this, MainAdminActivity.class));
                                } else {
                                    userRef.child("role").setValue("user");
                                    role = "user";
                                    startActivity(new Intent(SignUpUserActivity.this, MainActivity.class));
                                }

                                // Lưu thông tin vào SharedPreferences
                                SharedPrefManager.getInstance(this).saveUser(email, firstName + " " + lastName, role);
                                finish();
                            }
                            binding.progressBar.setVisibility(View.GONE);
                            binding.tvBtn.setVisibility(View.VISIBLE);
                            binding.layoutBottomSignup.setVisibility(View.GONE);
                            binding.layoutBottomSpace.setVisibility(View.VISIBLE);
                            binding.layoutSignupSuccess.setVisibility(View.VISIBLE);
                            binding.tvNameUser.setText(new StringBuilder().append(user.getFirst_name())
                                    .append(" ").append(user.getLast_name()));
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.tvBtn.setVisibility(View.VISIBLE);
                            Snackbar.make(binding.layoutBottomSignup, "Đăng ký thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }





}