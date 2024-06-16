package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.example.toysshop.untils.OrderNumberGenerator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private  String deliveryDate,orderDate;
    private CartDao cartDao;
    private CartDatabase cartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iNit();

        binding.tvSeenAllPayment.setOnClickListener(v->{
            showPaymentMethods();

        });


        checkedCartItems = getIntent().getParcelableArrayListExtra("checkedCartItems");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
        adapter = new ProductOrderAdapter(checkedCartItems);
        binding.recyclerview.setAdapter(adapter);
        DecimalFormat format = new DecimalFormat("#,###");
        binding.tvSum.setText(new StringBuilder().append(format.format(totalPrice)).append("đ"));
        binding.tvPriceDelivery.setText(new StringBuilder().append(format.format(22000)).append("đ"));
        binding.tvPriceTotal.setText(new StringBuilder().append(format.format(totalPrice+22000)).append("đ"));
        binding.tvSumTotalPrice.setText(new StringBuilder().append(format.format(totalPrice+22000)).append("đ"));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        orderDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        deliveryDate = dateFormat.format(calendar.getTime());

        binding.tvTimeDelivery.setText(new StringBuilder("Đơn hàng sẽ được giao vào ngày, ").append(deliveryDate));

        binding.btnOrder.setOnClickListener(v->{
            binding.tvBtn.setVisibility(View.GONE);
            binding.progressBtn.setVisibility(View.VISIBLE);
            placeOrder();

        });










    }

    private void placeOrder() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            String orderNumber = OrderNumberGenerator.generateOrderNumber();
            Order order = new Order(orderNumber,userId, checkedCartItems, totalPrice + 22000, orderDate,deliveryDate, "Chờ xác nhận");
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
            ordersRef.child(orderNumber).setValue(order)
                    .addOnCompleteListener(taskOrder->{
                        if(taskOrder.isSuccessful()){

                            new Thread(() -> {
                                cartDao.deleteCheckedCartItems(userId);
                                runOnUiThread(() -> {
                                    // Chuyển sang OrderSuccessActivity hoặc màn hình khác nếu cần
                                    Intent intent = new Intent(this, OrderSuccessActivity.class); // Tạo activity hiển thị thông tin đặt hàng thành công
                                    startActivity(intent);
                                    finish(); // Kết thúc activity hiện tại
                                });
                            }).start();

                        }
                        else{
                            binding.progressBtn.setVisibility(View.GONE);
                            binding.tvBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Đặt hàng thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            binding.progressBtn.setVisibility(View.GONE);
            binding.tvBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Bạn chưa đăng nhập, vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
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
            // Xử lý sự kiện khi người dùng chọn một phương thức thanh toán
            // Ví dụ:
            String selectedMethod = paymentMethods[position];
            // Do something with selectedMethod
            binding.tvMethodPayment.setText(selectedMethod);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();


    }


    private void iNit() {
        cartDatabase = CartDatabase.getInstance(this);
        cartDao = cartDatabase.cartDao();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}