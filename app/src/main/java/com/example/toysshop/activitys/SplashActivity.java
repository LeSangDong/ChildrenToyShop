package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.imageView.setVisibility(View.GONE);

            }
        },3000);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressBar.setVisibility(View.VISIBLE);

                if(currentUser != null){
                    DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference("account")
                            .child(currentUser.getUid());
                    loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String role = snapshot.child("role").getValue(String.class);
                                if(role != null){
                                    if(role.equals("admin")){
                                        startActivity(new Intent(SplashActivity.this, MainAdminActivity.class));
                                        finish();
                                    }
                                    else{
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                else{
                    startActivity(new Intent(SplashActivity.this, GetStartedActivity.class));
                    finish();
                }


            }
        },3000);
    }
}