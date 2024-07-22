package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
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
import com.example.toysshop.databinding.ActivityUpdateProductAdminBinding;
import com.example.toysshop.model.Toy;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdateProductAdminActivity extends AppCompatActivity {
    private ActivityUpdateProductAdminBinding binding;
    private Toy toy;
    private List<String> mListImage;
    private ImageAdapter imageAdapter;
    private ImageFirebaseAdapter imageFirebaseAdapter;
    private Uri imageUri;
    private final ArrayList<Uri> imagesUri = new ArrayList<>();
    private static final int Read_Permission = 101;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private int currentId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProductAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v->{
            startActivity(new Intent(this, MainAdminActivity.class));
        });

        if (getIntent().hasExtra("toy")) {
            toy = (Toy) getIntent().getSerializableExtra("toy");
            assert toy != null;
            binding.edtNameProduct.setText(toy.getTitle());
            binding.edtPriceProduct.setText(new StringBuilder().append(toy.getPrice()));
            binding.edtDescriptionProduct.setText(toy.getDescription());
            binding.edtColor.setText(toy.getColor_product());
            binding.edtSizeProduct.setText(toy.getSize_product());
            binding.edtThuonghieuProduct.setText(toy.getTrademark());
            if(toy.isBestToy()){
                binding.checkBestProduct.setChecked(true);
            }
            binding.edtPersentProduct.setText(new StringBuilder().append(toy.getPriceDiscount()));
            mListImage = toy.getImageList();
            imageFirebaseAdapter = new ImageFirebaseAdapter(mListImage);
            binding.recyclerviewFirebase.setHasFixedSize(true);
            binding.recyclerviewFirebase.setLayoutManager(new GridLayoutManager(this,2));
            binding.recyclerviewFirebase.setAdapter(imageFirebaseAdapter);





        }

        binding.btnUpdate.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progressLoading.setVisibility(View.VISIBLE);
            infoUpdateProduct();
        });

        if (ContextCompat.checkSelfPermission(UpdateProductAdminActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateProductAdminActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

        binding.btnUpload.setOnClickListener(v -> openGallery());

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        handleGalleryResult(data);
                    }
                });
    }

    private void handleGalleryResult(Intent data) {
        if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                imageUri = item.getUri();
                imagesUri.add(imageUri);
            }
        } else if (data.getData() != null) {
            imageUri = data.getData();
            imagesUri.add(imageUri);
        }
       // imageAdapter.notifyDataSetChanged();
        binding.recyclerview.setVisibility(View.VISIBLE);
        binding.recyclerviewFirebase.setVisibility(View.GONE);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new GridLayoutManager(this,2));
       imageAdapter = new ImageAdapter(imagesUri);
       binding.recyclerview.setAdapter(imageAdapter);

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(intent);
        binding.recyclerviewFirebase.setVisibility(View.GONE);
    }

    private void infoUpdateProduct() {
        String productName = binding.edtNameProduct.getText().toString().trim();
        String productPrice = binding.edtPriceProduct.getText().toString().trim();
        String description = binding.edtDescriptionProduct.getText().toString().trim();
        String color = binding.edtColor.getText().toString().trim();
        String size = binding.edtSizeProduct.getText().toString().trim();
        String trademark = binding.edtThuonghieuProduct.getText().toString().trim();
        boolean bestToy = binding.checkBestProduct.isChecked();
        String percent = binding.edtPersentProduct.getText().toString().trim();

        if (productName.isEmpty() || productPrice.isEmpty() || description.isEmpty() || color.isEmpty() ||
                size.isEmpty() || trademark.isEmpty() || percent.isEmpty()) {
            Snackbar.make(binding.layoutMain, "Vui lòng điền đầy đủ thông tin", Snackbar.LENGTH_SHORT).show();
            return;
        }

        uploadImagesAndUpdateProduct(productName, productPrice, description, color, size, trademark, bestToy, percent);
    }

    private void uploadImagesAndUpdateProduct(String productName, String productPrice, String description, String color,
                                              String size, String trademark, boolean bestToy, String percent) {
        if (imagesUri.isEmpty()) {
            updateProductInDatabase(productName, productPrice, description, color, size, trademark, bestToy, percent, new ArrayList<>(mListImage));
            return;
        }

        ArrayList<String> newImageUrls = new ArrayList<>();
        for (Uri uri : imagesUri) {
            uploadImageToFirebase(uri, newImageUrls, () -> {
                if (newImageUrls.size() == imagesUri.size()) {
                    updateProductInDatabase(productName, productPrice, description, color, size, trademark, bestToy, percent, newImageUrls);
                }
            });
        }
    }

    private void uploadImageToFirebase(Uri imageUri, ArrayList<String> imageUrls, Runnable onComplete) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images/" + imageUri.getLastPathSegment());

        imagesRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrls.add(uri.toString());
                    if (imageUrls.size() == imagesUri.size()) {
                        onComplete.run();
                    }
                })
        ).addOnFailureListener(exception -> {
            // Xử lý khi đẩy ảnh lên không thành công
            Toast.makeText(UpdateProductAdminActivity.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProductInDatabase(String productName, String productPrice, String description, String color,
                                         String size, String trademark, boolean bestToy, String percent, ArrayList<String> imageUrls) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Toy").child(String.valueOf(toy.getId()));

        HashMap<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("title", productName);
        productUpdates.put("description", description);
        productUpdates.put("price", Double.parseDouble(productPrice.replace(",", "")));
        productUpdates.put("color_product", color);
        productUpdates.put("size_product", size);
        productUpdates.put("trademark", trademark);
        productUpdates.put("bestToy", bestToy);
        productUpdates.put("priceDiscount", Double.parseDouble(percent));
        productUpdates.put("imageList", imageUrls);

        productRef.updateChildren(productUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateProductAdminActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                binding.tvBtn.setVisibility(View.VISIBLE);
                binding.progressLoading.setVisibility(View.GONE);
            } else {
                Toast.makeText(UpdateProductAdminActivity.this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainAdminActivity.class));
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
}
