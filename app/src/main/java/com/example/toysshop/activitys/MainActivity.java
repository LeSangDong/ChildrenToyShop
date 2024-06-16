package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.toysshop.R;
import com.example.toysshop.databinding.ActivityMainBinding;
import com.example.toysshop.fragments.CartFragment;
import com.example.toysshop.untils.UpdateCartEvent;
import com.google.android.material.navigation.NavigationBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private ActivityMainBinding binding;
    private NavController navController;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set background statusbar
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        navController = Navigation.findNavController(this,R.id.fragment_container_view);
        NavigationUI.setupWithNavController(binding.bottomNavigationview,navController);

        binding.bottomNavigationview.setOnItemSelectedListener(this);


        int getRequest = getIntent().getIntExtra("gotocart", 0);
        if(getRequest > 0){
            navController.navigate(R.id.cartFragment);
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.homeFragment){
            navController.navigate(R.id.homeFragment);
            return true;
        }
       else if(item.getItemId() == R.id.chatFragment){
             navController.navigate(R.id.chatFragment);
            return true;
        }
        else if(item.getItemId() == R.id.cartFragment){
            navController.navigate(R.id.cartFragment);
            return true;
        }
        else if(item.getItemId() == R.id.userFragment){
            navController.navigate(R.id.userFragment);
            return true;
        }
        return false;
    }
}