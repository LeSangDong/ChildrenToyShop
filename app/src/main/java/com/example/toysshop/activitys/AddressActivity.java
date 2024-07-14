package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.databinding.ActivityAddressBinding;
import com.example.toysshop.database.UserRepository;
import com.example.toysshop.model.User1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddressActivity extends AppCompatActivity {
    private ActivityAddressBinding binding;
    private UserRepository userRepository;
    private String phone;

    private boolean isPhoneVerified = false;

    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository(this);

        auth = FirebaseAuth.getInstance();

        binding.iconLocation.setOnClickListener(v->{
            startActivity(new Intent(this, HomeAddressActivity.class));
        });

        fetchAddress();


        binding.countryPicker.registerCarrierNumberEditText(binding.edtPhone);



        binding.btnPhone.setOnClickListener(v -> {


            binding.tvBtn1.setVisibility(View.GONE);
            binding.progress1.setVisibility(View.VISIBLE);

            if (isPhoneVerified) {
                Toast.makeText(this, "Số điện thoại đã được xác thực!", Toast.LENGTH_SHORT).show();
                binding.progress1.setVisibility(View.GONE);
                binding.tvBtn1.setVisibility(View.VISIBLE);
                return;
            }

            if (!binding.countryPicker.isValidFullNumber()) {
                binding.edtPhone.setError("Số điện thoại không chính xác!");
                binding.progress1.setVisibility(View.GONE);
                binding.tvBtn1.setVisibility(View.VISIBLE);
                return;
            }



            // Chuyển sang SendOTPActivity
            Intent intent = new Intent(AddressActivity.this, SendOTPActivity.class);
            intent.putExtra("phone", binding.countryPicker.getFullNumberWithPlus());
            startActivity(intent);
            binding.progress1.setVisibility(View.GONE);
            binding.tvBtn1.setVisibility(View.VISIBLE);
        });


        //Nhan phone verified from sendOTP
        phone = getIntent().getStringExtra("phone_verified");
        if(phone != null){
            binding.edtPhone.setText(phone);

            userRepository.insertUser(phone,"");

            User1 user = userRepository.getUserByPhone(phone);
            if (user != null) {
                isPhoneVerified = true; // Đánh dấu số điện thoại đã được xác thực
            }

        }

        binding.btnSave.setOnClickListener(v->{
            binding.tvBtn2.setVisibility(View.GONE);
            binding.progress2.setVisibility(View.VISIBLE);


            if(binding.edtPhone.getText().toString().isEmpty()){
                Toast.makeText(this, "Chưa nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                binding.progress2.setVisibility(View.GONE);
                binding.tvBtn2.setVisibility(View.VISIBLE);
                return;
            }
            if (!isPhoneVerified) {
                Toast.makeText(this, "Vui lòng xác thực số điện thoại!", Toast.LENGTH_SHORT).show();
                binding.progress2.setVisibility(View.GONE);
                binding.tvBtn2.setVisibility(View.VISIBLE);
                return;
            }
            FirebaseUser currentUser = auth.getCurrentUser();
            if(currentUser != null){
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
                dataRef.child("phone").setValue(binding.edtPhone.getText().toString().trim()).addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Đã lưu số điện thoại", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddressActivity.this, ConfirmOrderActivity.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        binding.progress2.setVisibility(View.GONE);
                        binding.tvBtn2.setVisibility(View.VISIBLE);
                    }

                });

            }
            else{
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                binding.progress2.setVisibility(View.GONE);
                binding.tvBtn2.setVisibility(View.VISIBLE);
            }





            if(binding.edtAddress.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Địa chỉ rỗng", Toast.LENGTH_SHORT).show();
                binding.progress2.setVisibility(View.GONE);
                binding.tvBtn2.setVisibility(View.VISIBLE);
                return;
            }
            if(currentUser != null){
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
                dataRef.child("address").setValue(binding.edtAddress.getText().toString().trim()).addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        binding.progress2.setVisibility(View.GONE);
                        binding.tvBtn2.setVisibility(View.VISIBLE);


                    }
                    else{
                        binding.progress2.setVisibility(View.GONE);
                        binding.tvBtn2.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }

                });

            }
            else{
                binding.progress2.setVisibility(View.GONE);
                binding.tvBtn2.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            }



        });





    }

    private void fetchAddress() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference phoneRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
            phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        String address = snapshot.child("address").getValue(String.class);
                        if(address != null){
                            binding.edtAddress.setText(address);
                        }
                        else{
                            binding.edtAddress.setText("");
                        }



                    }
                    else{
                        binding.edtAddress.setText("");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else{
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }


    }


}
