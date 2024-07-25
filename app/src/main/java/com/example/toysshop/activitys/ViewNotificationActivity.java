package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.NotificationAdapter;
import com.example.toysshop.databinding.ActivityViewNotificationBinding;
import com.example.toysshop.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewNotificationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityViewNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private List<Order> orderList;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth  = FirebaseAuth.getInstance();
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));


        loadNotifications();




    }

    private void loadNotifications() {
        orderList = new ArrayList<>();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            String userId = currentUser.getUid();
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
            ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && "đã bị từ chối".equals(order.getStatus()) || "đang vận chuyển".equals(order.getStatus())) {
                            orderList.add(order);
                        }
                    }
                    if(orderList.size() == 0){
                        binding.tvEmptyNotification.setVisibility(View.VISIBLE);
                    }
                    notificationAdapter = new NotificationAdapter(orderList);
                    binding.recyclerview.setAdapter(notificationAdapter);
                    binding.swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewNotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                    binding.swipeRefreshLayout.setRefreshing(false);

                }
            });

        }
        else{
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            binding.swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onRefresh() {
        loadNotifications();

    }
}