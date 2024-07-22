package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityHomeAddressBinding;
import com.example.toysshop.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HomeAddressActivity extends AppCompatActivity {
    private ActivityHomeAddressBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.iconLocation.setOnClickListener(v->{
            startActivity(new Intent(this, MapsActivity.class));

        });

        String address = getIntent().getStringExtra("address");
        if(address != null){
            binding.edtAddress.setText(address);
        }

       binding.btnSave.setOnClickListener(v->{
           binding.tvBtn2.setVisibility(View.GONE);
           binding.progress2.setVisibility(View.VISIBLE);
           if(binding.edtAddress.getText().toString().trim().isEmpty()){
               Toast.makeText(this, "Vui lòng hoàn thành địa chỉ nhận hàng!", Toast.LENGTH_SHORT).show();
               binding.progress2.setVisibility(View.GONE);
               binding.tvBtn2.setVisibility(View.VISIBLE);
               return;


           }
           HashMap<String,Object> hashMap = new HashMap<>();
           hashMap.put("address",binding.edtAddress.getText().toString().trim());
           FirebaseUser currentUser = auth.getCurrentUser();
           if(currentUser != null){
               DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
               dataRef.setValue(hashMap).addOnCompleteListener(task->{
                   if(task.isSuccessful()){
                       binding.progress2.setVisibility(View.GONE);
                       binding.tvBtn2.setVisibility(View.VISIBLE);
                   finish();

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
}