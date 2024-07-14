package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.PaymentMethodAdapter;
import com.example.toysshop.adapter.ProductOrderAdapter;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.ActivityConfirmOrderBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ConfirmOrderActivity extends AppCompatActivity {
    private ActivityConfirmOrderBinding binding;
    private ProductOrderAdapter adapter;
    private ArrayList<CartModel> checkedCartItems;
    private double totalPrice;
    private String deliveryDate, orderDate;
    private CartDao cartDao;
    private CartDatabase cartDatabase;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        binding.ivNext.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmOrderActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        binding.tvSeenAllPayment.setOnClickListener(v -> {
            showPaymentMethods();
        });

        fetchUserData();

        checkedCartItems = getIntent().getParcelableArrayListExtra("checkedCartItems");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0);

        if (checkedCartItems != null) {
            saveCheckedCartItems();
        } else {
            restoreCheckedCartItems();
        }

        adapter = new ProductOrderAdapter(checkedCartItems);
        binding.recyclerview.setAdapter(adapter);

        updatePriceViews();

        binding.btnOrder.setOnClickListener(v -> {
            handlePlaceOrder();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCheckedCartItems();
    }

    private void saveCheckedCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("checkedCartItemsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(checkedCartItems);
        editor.putString("checkedCartItems", json);
        editor.putFloat("totalPrice", (float) totalPrice);
        editor.apply();
    }

    private void restoreCheckedCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("checkedCartItemsPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("checkedCartItems", null);
        Type type = new TypeToken<ArrayList<CartModel>>() {}.getType();
        checkedCartItems = gson.fromJson(json, type);
        totalPrice = sharedPreferences.getFloat("totalPrice", 0);

        if (checkedCartItems == null) {
            checkedCartItems = new ArrayList<>();
        }
    }

    private void updatePriceViews() {
        DecimalFormat format = new DecimalFormat("#,###");
        binding.tvSum.setText(new StringBuilder().append(format.format(totalPrice)).append("đ"));
        binding.tvPriceDelivery.setText(new StringBuilder().append(format.format(22000)).append("đ"));
        binding.tvPriceTotal.setText(new StringBuilder().append(format.format(totalPrice + 22000)).append("đ"));
        binding.tvSumTotalPrice.setText(new StringBuilder().append(format.format(totalPrice + 22000)).append("đ"));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        orderDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        deliveryDate = dateFormat.format(calendar.getTime());
        binding.tvTimeDelivery.setText(new StringBuilder("Đơn hàng sẽ được giao vào ngày, ").append(deliveryDate));
    }

    private void fetchUserData() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account")
                    .child(currentUser.getUid()).child("info");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("name").getValue(String.class);
                        binding.tvNameUser.setText(userName);
                    } else {
                        Toast.makeText(ConfirmOrderActivity.this, "Không tìm thấy account.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            DatabaseReference phoneRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
            phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String phone = snapshot.child("phone").getValue(String.class);
                        String address = snapshot.child("address").getValue(String.class);
                        if(phone != null){
                            binding.ivPhone.setVisibility(View.VISIBLE);
                            binding.tvPhone.setText(phone);
                            binding.iconTickSuccessPhone.setVisibility(View.VISIBLE);
                        }
                        binding.tvAddress.setText(address);
                    } else {
                        binding.tvAddress.setText(new StringBuilder().append(""));
                        binding.tvPhone.setText(new StringBuilder().append(""));
                        binding.iconTickSuccessPhone.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPaymentMethods() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_payment, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        ListView listViewPaymentMethods = view.findViewById(R.id.listViewPaymentMethods);
        String[] paymentMethods = {"Thanh toán tiền mặt", "Thanh toán MoMo", "Thanh toán Agribank"};
        int[] paymentIcons = {R.drawable.baseline_money_24, R.drawable.icon_momo, R.drawable.icon_bank};

        PaymentMethodAdapter adapter = new PaymentMethodAdapter(this, paymentMethods, paymentIcons);
        listViewPaymentMethods.setAdapter(adapter);

        listViewPaymentMethods.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedMethod = paymentMethods[position];
            binding.tvMethodPayment.setText(selectedMethod);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void handlePlaceOrder() {
        if (binding.tvPhone.getText().toString().trim().isEmpty() || binding.tvAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Bạn cần xác thực số điện thoại và địa chỉ trước khi đặt hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.tvBtn.setVisibility(View.GONE);
        binding.progressBtn.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            long timestamp = System.currentTimeMillis();

            Order order = new Order(
                    String.valueOf(timestamp),
                    userId,
                    checkedCartItems,
                    totalPrice + 22000,
                    orderDate,
                    deliveryDate,
                    "Chờ xác nhận",
                    "",
                    binding.tvPhone.getText().toString().trim(),
                    binding.tvNameUser.getText().toString().trim(),
                    binding.tvAddress.getText().toString().trim());

            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
            ordersRef.child(String.valueOf(timestamp)).setValue(order)
                    .addOnCompleteListener(taskOrder -> {
                        if (taskOrder.isSuccessful()) {
                            new Thread(() -> {
                                cartDao.deleteCheckedCartItems(userId);
                                runOnUiThread(() -> {
                                    // Clear SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("checkedCartItemsPrefs", MODE_PRIVATE);
                                    sharedPreferences.edit().clear().apply();

                                    Intent intent = new Intent(ConfirmOrderActivity.this, OrderSuccessActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            }).start();
                        } else {
                            binding.progressBtn.setVisibility(View.GONE);
                            binding.tvBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(ConfirmOrderActivity.this, "Đặt hàng thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            binding.progressBtn.setVisibility(View.GONE);
            binding.tvBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Bạn chưa đăng nhập, vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        binding = ActivityConfirmOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartDatabase = CartDatabase.getInstance(this);
        cartDao = cartDatabase.cartDao();
        auth = FirebaseAuth.getInstance();

        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}
