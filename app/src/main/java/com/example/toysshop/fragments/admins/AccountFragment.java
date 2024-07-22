package com.example.toysshop.fragments.admins;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.AdvertimentActivity;
import com.example.toysshop.activitys.DoanhThuActivity;
import com.example.toysshop.activitys.GetStartedActivity;
import com.example.toysshop.activitys.InfoAdminActivity;
import com.example.toysshop.activitys.MainAdminActivity;
import com.example.toysshop.activitys.ReplacePasswordActivity;
import com.example.toysshop.databinding.FragmentAccountBinding;
import com.example.toysshop.untils.OrderCountEvent;
import com.example.toysshop.untils.UpdateCartEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class AccountFragment extends Fragment {

   private FragmentAccountBinding binding;
   private NavController navController;
   private FirebaseAuth auth;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrderCountEvent(OrderCountEvent event) {
        int orderCount = event.getOrderCount();
        binding.tvNotifiicationCart.setText(new StringBuilder().append(orderCount));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentAccountBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init
        initialized(view);

        binding.itemLogout.setOnClickListener(v->{
            auth.signOut();
            startActivity(new Intent(requireContext(), GetStartedActivity.class));

        });
        binding.itemDoanhthu.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), DoanhThuActivity.class));
        });
        binding.itemReplacePass.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), ReplacePasswordActivity.class));
        });
        binding.itemUpdateInfo.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), InfoAdminActivity.class));
        });
        binding.itemAdvertiment.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), AdvertimentActivity.class));
        });

        //fetch image and name admin
        fetchInfo();

        binding.fab.setOnClickListener(v->{
            navController.navigate(R.id.orderFragment);
        });

    }

    private void fetchInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
         DatabaseReference infoRef =  FirebaseDatabase.getInstance().getReference("account").child(currentUser.getUid());
         infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 String role = snapshot.child("role").getValue(String.class);
                 if("admin".equals(role)){
                     String name = snapshot.child("info").child("name").getValue(String.class);
                     String url = snapshot.child("info").child("url").getValue(String.class);
                     if(name != null){
                         binding.collapsingToolbar.setTitle(name);
                     }
                     if(url != null){
                         Glide.with(requireContext())
                                 .load(url).placeholder(R.drawable.imag_user_default).into(binding.imgAvatar);
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

        }
    }

    private void initialized(View view) {
        navController = Navigation.findNavController(view);
        auth = FirebaseAuth.getInstance();
    }
}