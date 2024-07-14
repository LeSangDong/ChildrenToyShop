package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityViewInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ViewInfoActivity extends AppCompatActivity {
    private ActivityViewInfoBinding binding;
    private FirebaseAuth auth;
    private Uri imageUri;
    private  String avatarUrl;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khoi tao
        iNit();
        loadUserInfo();
        loadInfoAddress();
        binding.iconAdd.setOnClickListener(v->{
            openGallery();
        });
        binding.imageAvatar.setOnClickListener(v->{
            Intent intent = new Intent(this, SeenImageActivity.class);
            intent.putExtra("image_item",avatarUrl);
            startActivity(intent);
        });

        //khoi tao gallery
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        binding.imageAvatar.setImageURI(imageUri);
                        uploadImageToFirebase();
                    }
                });


    }

    private void loadInfoAddress() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String address = snapshot.child("address").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        binding.tvAddress.setText(address);
                        assert phone != null;
                        binding.tvPhone.setText(formatPhoneNumber(phone));
                    }
                    else{
                        binding.tvPhone.setText(Html.fromHtml("<i>Chưa xác thực số điện thoại  </i>", Html.FROM_HTML_MODE_LEGACY));
                        binding.tvAddress.setText(Html.fromHtml("<i>Chưa xác thực địa chỉ  </i>", Html.FROM_HTML_MODE_LEGACY));
                        binding.checkSuccess.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else{
            Toast.makeText(this, "Bạn chưa dăng nhập", Toast.LENGTH_SHORT).show();
        }
    }
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+84")) {
            return "0" + phoneNumber.substring(3);
        }
        return phoneNumber;
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account").child(userId).child("info");

            // Load avatar URL and user name
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                       avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                        String userName = snapshot.child("name").getValue(String.class);

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(ViewInfoActivity.this).load(avatarUrl).placeholder(R.drawable.imag_user_default).into(binding.imageAvatar);
                        }

                        if (userName != null && !userName.isEmpty()) {
                            binding.tvUserName.setText(userName);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewInfoActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImageToFirebase() {
        binding.progressLoading.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("avatars/" + userId + ".jpg");
                UploadTask uploadTask = storageRef.putFile(imageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    saveImageUrlToDatabase(downloadUrl);
                    binding.progressLoading.setVisibility(View.GONE);
                })).addOnFailureListener(e ->{
                    binding.progressLoading.setVisibility(View.GONE);
                    Toast.makeText(ViewInfoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


            }
        }

    }

    private void saveImageUrlToDatabase(String imageUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account").child(userId).child("info");
            userRef.child("avatarUrl").setValue(imageUrl).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewInfoActivity.this, "Đã tải ảnh lên", Toast.LENGTH_SHORT).show();
                    Glide.with(ViewInfoActivity.this).load(imageUrl).into(binding.imageAvatar);
                } else {
                    Toast.makeText(ViewInfoActivity.this, "Failed to update avatar", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);

    }

    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
}