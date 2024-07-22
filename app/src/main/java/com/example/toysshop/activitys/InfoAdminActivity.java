package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityInfoAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class InfoAdminActivity extends AppCompatActivity {
    private ActivityInfoAdminBinding binding;
    private FirebaseAuth auth;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iNit();


        binding.btnBack.setOnClickListener(v->{
            finish();
        });

        fetInfoAdmin();
        binding.iconEdit.setOnClickListener(v->{
            String nameUpdate = binding.edtName.getText().toString().trim();
            if(nameUpdate.isEmpty()){
                Toast.makeText(this, "Tên đang rỗng", Toast.LENGTH_SHORT).show();
                return;
            }
            updateUserName(nameUpdate);
        });
        binding.iconEditPhone.setOnClickListener(v->{
            String phoneUpdate = binding.edtPhone.getText().toString().trim();
            if(phoneUpdate.isEmpty()){
                Toast.makeText(this, "Số điện thoại đang rỗng", Toast.LENGTH_SHORT).show();
                return;
            }
            updatePhone(phoneUpdate);
        });
        binding.iconAdd.setOnClickListener(v->{
            openGallery();
        });

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        binding.imageAvatar.setImageURI(imageUri);
                        uploadImageToStorage();
                    }
                }
        );

    }

    private void uploadImageToStorage() {
        binding.progress.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars").child(currentUser.getUid() + ".jpg");
                storageRef.putFile(imageUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> updateProfileImageUri(uri.toString()));
                    } else {
                        binding.progress.setVisibility(View.GONE);
                        Toast.makeText(InfoAdminActivity.this, "Upload image failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void updateProfileImageUri(String uri) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("account").child(currentUser.getUid()).child("info");
            Map<String, Object> updates = new HashMap<>();
            updates.put("url", uri);

            infoRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(InfoAdminActivity.this, "Đã tải ảnh lên", Toast.LENGTH_SHORT).show();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(InfoAdminActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        resultLauncher.launch(intent);

    }

    private void updatePhone(String phoneUpdate) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("account").child(currentUser.getUid()).child("info");
            Map<String, Object> updates = new HashMap<>();
            updates.put("phone", phoneUpdate);

            infoRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(InfoAdminActivity.this, "Cập nhật số điện thoại thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InfoAdminActivity.this, "Cập nhật số điện thoại thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void updateUserName(String newName) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("account").child(currentUser.getUid()).child("info");
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);

            infoRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(InfoAdminActivity.this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InfoAdminActivity.this, "Cập nhật tên thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetInfoAdmin() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("account").child(currentUser.getUid());
            infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   String role = snapshot.child("role").getValue(String.class);
                   if("admin".equals(role)){
                       String name = snapshot.child("info").child("name").getValue(String.class);
                       String phone = snapshot.child("info").child("phone").getValue(String.class);
                       String url = snapshot.child("info").child("url").getValue(String.class);
                       if(name != null){
                           binding.edtName.setText(name);
                       }
                       if(phone != null){
                           binding.edtPhone.setText(phone);
                       }
                       if(url != null){
                           Glide.with(InfoAdminActivity.this)
                                   .load(url)
                                   .placeholder(R.drawable.imag_user_default).into(binding.imageAvatar);
                       }
                   }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
}