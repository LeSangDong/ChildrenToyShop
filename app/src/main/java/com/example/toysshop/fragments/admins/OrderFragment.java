package com.example.toysshop.fragments.admins;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.OrderAdminAdapter;
import com.example.toysshop.databinding.FragmentOrderBinding;
import com.example.toysshop.model.Order;
import com.example.toysshop.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentOrderBinding binding;
    private OrderAdminAdapter orderAdminAdapter;
    private List<Order> mList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iNit();

        fetchAllOrder();


    }

    private void iNit() {
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void fetchAllOrder() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && "Chờ xác nhận".equals(order.getStatus())) {
                            loadUserInfo(order.getUserId(), order);
                            mList.add(order);
                            Log.e("LOI","");
                        }
                    }
                }
                if(mList.isEmpty()){
                    binding.tvNoOrder.setVisibility(View.VISIBLE);
                }
                if (orderAdminAdapter == null) {
                    orderAdminAdapter = new OrderAdminAdapter(mList);
                    binding.recyclerview.setAdapter(orderAdminAdapter);
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    orderAdminAdapter.notifyDataSetChanged();
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("OrderFragment", "Error fetching orders: " + error.getMessage());
                Toast.makeText(requireContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadUserInfo(String userId, Order order) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    order.setAvatar_user("");
                    orderAdminAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("OrderFragment", "Error loading user info: " + error.getMessage());
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onRefresh() {
        fetchAllOrder();

    }
}