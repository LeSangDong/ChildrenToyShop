package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.WaitOrderAdapter;
import com.example.toysshop.databinding.ActivityDeliveryOrderBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityDeliveryOrderBinding binding;

    private FirebaseAuth auth;
    private List<Order> orderList = new ArrayList<Order>();
    private WaitOrderAdapter waitOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliveryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        iNit();
        loadOrder();




    }

    private void iNit() {
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);
        auth = FirebaseAuth.getInstance();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadOrder() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getUid());
            Query query = orderRef.orderByChild("status").equalTo("đang vận chuyển");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        orderList.clear();
                        for(DataSnapshot orderSnapshot : snapshot.getChildren()){
                            Order order = new Order();
                            order.setOrderId(orderSnapshot.getKey());
                            order.setUserId(currentUser.getUid());
                            order.setStatus(orderSnapshot.child("status").getValue(String.class));
                            order.setOrderDate(orderSnapshot.child("orderDate").getValue(String.class));


                            List<CartModel> cartItems = new ArrayList<>();

                            for(DataSnapshot cartItemSnapshot: orderSnapshot.child("cartItems").getChildren()){
                                CartModel cartItem = cartItemSnapshot.getValue(CartModel.class);
                                if(cartItem != null){
                                    cartItems.add(cartItem);
                                }
                            }
                            order.setCartItems(cartItems);
                            orderList.add(order);

                        }
                        waitOrderAdapter = new WaitOrderAdapter(orderList);
                        binding.recyclerview.setAdapter(waitOrderAdapter);
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                    else{
                        binding.tvNoOrder.setVisibility(View.VISIBLE);
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(DeliveryOrderActivity.this, "Lỗi khi tải đơn hàng", Toast.LENGTH_SHORT).show();

                }
            });

        }
        else{
            binding.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefresh() {
        loadOrder();

    }
}