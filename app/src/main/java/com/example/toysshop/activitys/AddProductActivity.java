package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.ColorCheckAdapter;
import com.example.toysshop.adapter.ImageAdapter;
import com.example.toysshop.configs.ConfigDialog;
import com.example.toysshop.databinding.ActivityAddProductBinding;
import com.example.toysshop.model.ColorItem;
import com.example.toysshop.model.Toy;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddProductActivity extends AppCompatActivity {
    private ActivityAddProductBinding binding;
    private final ArrayList<Uri> imagesUri = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private Uri imageUri;
    private static final int Read_Permission = 101;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private int currentId = 0; // Biến lưu trữ ID hiện tại


    private ColorCheckAdapter colorCheckAdapter;
    private int[] colorArray;
    private String[] colorNames;
    private List<Boolean> selectedColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //set initial color
        colorArray = getResources().getIntArray(R.array.color_array);
        colorNames = getResources().getStringArray(R.array.name_colors);
        selectedColors = IntStream.range(0, colorNames.length).mapToObj(i -> false).collect(Collectors.toList());

        binding.colorRecyclerview.setHasFixedSize(true);
        binding.colorRecyclerview.setLayoutManager(new LinearLayoutManager(this));

      colorCheckAdapter = new ColorCheckAdapter(this, Arrays.asList(colorNames), colorArray, selectedColors);
        binding.colorRecyclerview.setAdapter(colorCheckAdapter);





        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        imageAdapter = new ImageAdapter(imagesUri);
        binding.recyclerview.setAdapter(imageAdapter);

        if (ContextCompat.checkSelfPermission(AddProductActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddProductActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

        binding.btnUpload.setOnClickListener(v -> openGallery());

        String[] categories = {"0-12 Tháng", "1-3 Tuổi", "3-6 Tuổi", "6-12 Tuổi", "Trên 12 Tuổi", "Dành cho bé trai", "Dành cho bé gái"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        binding.btnAdd.setOnClickListener(v ->
                {
                    ConfigDialog.showDialog(this);

                    checkValidInput();
                }
        );

        binding.switchDiscount.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                binding.layoutPersent.setVisibility(View.VISIBLE);
            } else {
                binding.layoutPersent.setVisibility(View.GONE);
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        handleGalleryResult(data);
                    }
                });

        // Lấy ID hiện tại từ Firebase
        getCurrentIdFromFirebase();
    }

    private void getCurrentIdFromFirebase() {
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("currentId");
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentId = snapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
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
        });
    }

    private void uploadImagesAndPushProduct(String name_product, String price_product, String description,  String color_product,String size_product,  String thuong_hieu, int categoryId) {
        if (imagesUri.isEmpty()) {
            Toast.makeText(AddProductActivity.this, "No images to upload", Toast.LENGTH_SHORT).show();
            return;
        }


        ArrayList<String> imageUrls = new ArrayList<>();
        for (Uri uri : imagesUri) {
            uploadImageToFirebase(uri, imageUrls, () -> {
                currentId++; // Tăng ID hiện tại
                // Lưu thông tin sản phẩm sau khi tất cả ảnh đã được tải lên
                pushProductToDatabase(name_product, price_product, description, color_product, size_product, thuong_hieu, categoryId, imageUrls);
                // Cập nhật ID hiện tại trong Firebase
                updateCurrentIdInFirebase();
            });
        }
    }


    private void updateCurrentIdInFirebase() {
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("currentId");
        idRef.setValue(currentId);
    }

    private void pushProductToDatabase(String name_product, String price_product, String description, String color_product, String size_product, String thuong_hieu, int categoryId, ArrayList<String> imageUrls) {
        // Tạo một đối tượng Toys mới
      Toy toy = new Toy();
        toy.setBestToy(true); // Cập nhật theo logic của bạn
        toy.setNewProduct(true); // Cập nhật theo logic của bạn
        toy.setId(currentId); // Sử dụng ID hiện tại
        toy.setLike(true); // Cập nhật theo logic của bạn
        toy.setStar(5); // Cập nhật theo logic của bạn
        toy.setTitle(name_product);
        toy.setPrice(Double.parseDouble(price_product));
        toy.setDescription(description);
        toy.setColor_product(color_product);
        toy.setSize_product(size_product);
        toy.setTrademark(thuong_hieu);
        toy.setCategoryId(categoryId);
        toy.setImageList(imageUrls); // Danh sách URL hình ảnh
        toy.setDiscount(binding.switchDiscount.isChecked()); // Kiểm tra trạng thái của switch
        toy.setPriceDiscount(binding.switchDiscount.isChecked() ? Integer.parseInt(binding.edtPersentProduct.getText().toString()) : 0); // Giá trị giảm giá

        // Thực hiện lưu thông tin sản phẩm lên Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Toy");
        databaseRef.child(String.valueOf(currentId)).setValue(toy)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xử lý khi lưu thông tin sản phẩm thành công
                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                    } else {
                        // Xử lý khi lưu thông tin sản phẩm không thành công
                        Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkValidInput() {
        String name_product = binding.edtNameProduct.getText().toString().trim();
        if (name_product.isEmpty()) {
            Snackbar.make(binding.layoutMain, "Vui lòng nhập tên sản phẩm", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String price_product = binding.edtPriceProduct.getText().toString().trim();
        if (price_product.isEmpty()) {
            Snackbar.make(binding.layoutMain, "Vui lòng nhập giá sản phẩm", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String description = binding.edtDescriptionProduct.getText().toString().trim();
        if (description.isEmpty()) {
            Snackbar.make(binding.layoutMain, "Vui lòng nhập mô tả sản phẩm", Snackbar.LENGTH_SHORT).show();
            return;
        }

        List<ColorItem> selectedColors = getSelectedColors();
        StringBuilder color_productBuilder = new StringBuilder();
        for (ColorItem colorItem : selectedColors) {
            color_productBuilder.append(colorItem.getColorName()).append(", ");
        }
        String color_product = color_productBuilder.toString();
        if (!color_product.isEmpty()) {
            color_product = color_product.substring(0, color_product.length() - 2);
        }


        String size_product = binding.edtSizeProduct.getText().toString().trim();
        if (size_product.isEmpty()) {
            Snackbar.make(binding.layoutMain, "Vui lòng nhập kích thước sản phẩm", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String thuong_hieu = binding.edtThuonghieuProduct.getText().toString().trim();
        if (thuong_hieu.isEmpty()) {
            Snackbar.make(binding.layoutMain, "Vui lòng nhập thương hiệu sản phẩm", Snackbar.LENGTH_SHORT).show();
            return;
        }




        int categoryId = binding.spinnerCategory.getSelectedItemPosition();
        if (categoryId < 0) {
            Snackbar.make(binding.layoutMain, "Vui lòng chọn danh mục sản phẩm", Snackbar.LENGTH_SHORT).show();
            return;
        }

        uploadImagesAndPushProduct(name_product, price_product, description,color_product,size_product, thuong_hieu, categoryId);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(intent);
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
        imageAdapter.notifyDataSetChanged();
    }

    private void clearInputFields() {
        binding.edtNameProduct.setText("");
        binding.edtPriceProduct.setText("");
        binding.edtDescriptionProduct.setText("");
        binding.edtSizeProduct.setText("");
        binding.edtThuonghieuProduct.setText("");
        binding.edtPersentProduct.setText("");
        binding.spinnerCategory.setSelection(0);
        binding.switchDiscount.setChecked(false);
        imagesUri.clear();
        imageAdapter.notifyDataSetChanged();
    }

    private List<ColorItem> getSelectedColors() {
        return colorCheckAdapter.getListColor();
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
