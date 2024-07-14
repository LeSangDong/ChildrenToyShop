package com.example.toysshop.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.adapter.SeenOrderedAdapter;
import com.example.toysshop.databinding.ActivitySeenDetailOrderedBinding;
import com.example.toysshop.model.CartModel;

import java.util.ArrayList;

public class SeenDetailOrderedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivitySeenDetailOrderedBinding binding;
    private ArrayList<CartModel> cartItems;
    private SeenOrderedAdapter seenOrderedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeenDetailOrderedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);
        cartItems = getIntent().getParcelableArrayListExtra("cartItems");
        binding.swipeRefreshLayout.setRefreshing(false);
        seenOrderedAdapter = new SeenOrderedAdapter(cartItems);

        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(seenOrderedAdapter);





    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }
}