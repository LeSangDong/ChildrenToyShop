package com.example.toysshop.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.toysshop.R;
import com.example.toysshop.accounts.SharedPrefManager;
import com.example.toysshop.databinding.ActivityLoginBinding;
import com.example.toysshop.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                        auth.signInWithCredential(credential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser currentUser = auth.getCurrentUser();
                                            if (currentUser != null) {
                                                String email = signInAccount.getEmail();
                                                String name = signInAccount.getDisplayName();
                                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account")
                                                        .child(currentUser.getUid());
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("email", email);
                                                hashMap.put("name", name);
                                                userRef.child("info").setValue(hashMap);

                                                userRef.child("role").setValue("user");
                                             String   role = "user";
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));



                                                // Lưu thông tin vào SharedPreferences
                                                SharedPrefManager.getInstance(getApplicationContext()).saveUser(email, name, role);
                                                finish();
                                            }
                                        } else {
                                            showErrorMessage("Đăng nhập không thành công: " + task.getException().getMessage());
                                        }
                                    }
                                });
                    } catch (ApiException e) {
                        showErrorMessage("Đăng nhập không thành công: " + e.getMessage());
                    }
                } else {
                    showErrorMessage("Đăng nhập không thành công: resultCode = " + result.getResultCode());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, options);
        auth = FirebaseAuth.getInstance();

        binding.btnLoginWithGoogle.setOnClickListener(v -> {
            binding.tvBtnGoogle.setVisibility(View.GONE);
            binding.progressGoogle.setVisibility(View.VISIBLE);
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });

        loginAccount();
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loginAccount() {
        binding.btnLogin.setOnClickListener(v -> {
            binding.tvBtn.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPass.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                showErrorMessage("Vui lòng nhập email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                showErrorMessage("Vui lòng nhập mật khẩu");
                return;
            }

            User user = new User(email, password);
            auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(this, task -> {
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
                                                // Lưu thông tin vào SharedPreferences và chuyển hướng
                                                String name = snapshot.child("info").child("name").getValue(String.class);
                                                saveUserAndRedirect(email, name, role);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        showErrorMessage("Đăng nhập không thành công, vui lòng thử lại sau!");
                                    }
                                });
                            }
                        } else {
                            showErrorMessage("Phiên đăng nhập đã hết hạn hoặc thông tin không chính xác");
                        }
                    });
        });
    }

    private void saveUserAndRedirect(String email, String name, String role) {
        SharedPrefManager.getInstance(LoginActivity.this).saveUser(email, name, role);
        if (role.equals("admin")) {
            startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        finish();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.GONE);
        binding.tvBtn.setVisibility(View.VISIBLE);
    }
}
