package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.ImageAdapter;
import com.example.toysshop.adapter.ImageFirebaseAdapter;
import com.example.toysshop.databinding.ActivityAdvertimentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdvertimentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityAdvertimentBinding binding;
    private final ArrayList<String> imageUrls = new ArrayList<>();
    private ImageFirebaseAdapter imageFirebaseAdapter;
    private final ArrayList<Uri> imagesUri = new ArrayList<>();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int Read_Permission = 101;
    private DatabaseReference bannerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdvertimentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        bannerRef = FirebaseDatabase.getInstance().getReference("banner").child("url");

        binding.btnBack.setOnClickListener(v -> finish());

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        handleGalleryResult(data);
                    }
                });

        binding.fab.setOnClickListener(v -> openGallery());

        fetchBannerImages();
    }

    private void fetchBannerImages() {
        binding.swipeRefreshLayout.setRefreshing(true);
        bannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageUrls.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    imageUrls.add(imageUrl);
                }
               imageFirebaseAdapter = new ImageFirebaseAdapter(imageUrls);
                binding.recyclerview.setHasFixedSize(true);
                binding.recyclerview.setLayoutManager(new GridLayoutManager(AdvertimentActivity.this,2));
                binding.recyclerview.setAdapter(imageFirebaseAdapter);
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AdvertimentActivity.this, "Failed to fetch images", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleGalleryResult(Intent data) {
        binding.swipeRefreshLayout.setRefreshing(true);
        if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri imageUri = item.getUri();
                imagesUri.add(imageUri);
            }
        } else if (data.getData() != null) {
            Uri imageUri = data.getData();
            imagesUri.add(imageUri);
        }
        if (!imagesUri.isEmpty()) {
            uploadImagesToFirebase();
        }
    }

    private void uploadImagesToFirebase() {
        if (imagesUri.isEmpty()) return;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        List<String> imageUrls = new ArrayList<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("banner").child("url");

        for (Uri imageUri : imagesUri) {
            StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.add(uri.toString());
                        if (imageUrls.size() == imagesUri.size()) {
                            databaseRef.setValue(imageUrls).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                   binding.swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(AdvertimentActivity.this, "Images uploaded successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(AdvertimentActivity.this, "Failed to upload images", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
            ).addOnFailureListener(e -> {
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AdvertimentActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Read_Permission) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchBannerImages();

    }
}
