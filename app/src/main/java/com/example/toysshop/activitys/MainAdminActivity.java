package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityMainAdminBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainAdminActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private ActivityMainAdminBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set background statusbar
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        navController = Navigation.findNavController(this,R.id.fragment_container_admin_view);
        NavigationUI.setupWithNavController(binding.bottomNavigationviewAdmin,navController);

        binding.bottomNavigationviewAdmin.setOnItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.productFragment){
            navController.navigate(R.id.productFragment);
            return true;
        }
        else if(item.getItemId() == R.id.contactFragment){
            navController.navigate(R.id.contactFragment);
            return true;
        }
        else if(item.getItemId() == R.id.orderFragment){
            navController.navigate(R.id.orderFragment);
            return true;
        }
        else if(item.getItemId() == R.id.accountFragment){
            navController.navigate(R.id.accountFragment);
            return true;
        }
        return false;
    }
}