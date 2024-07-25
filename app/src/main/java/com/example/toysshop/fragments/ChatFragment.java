package com.example.toysshop.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.ChatActivity;
import com.example.toysshop.databinding.FragmentChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChatFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentChatBinding binding;
    private String userId_admin;
    private  String url;
    private String name, phone;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iNit(view);

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);

        fetchInfoAdmin(new OnAdminInfoFetchedListener() {
            @Override
            public void onAdminInfoFetched(String userId) {
                userId_admin = userId;
                fetchAvatarAdmin(userId_admin);
            }
        });

        binding.itemChatWithAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("chatWithUserId", userId_admin);
            intent.putExtra("url",url);
            intent.putExtra("user_name",name);
            getContext().startActivity(intent);
        });
        binding.btnPhone.setOnClickListener(v->{
            if (phone != null && !phone.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                if (getContext().checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                }
            }
        });
        binding.btnBack.setOnClickListener(v -> {
            navController.navigateUp();

        });

    }

    private void iNit(View view) {
        navController = Navigation.findNavController(view);
    }

    private void fetchAvatarAdmin(String adminUserId) {
        DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("account").child(adminUserId);
        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("role").getValue(String.class);
                if ("admin".equals(role)) {
                   url = snapshot.child("info").child("url").getValue(String.class);
                    phone = snapshot.child("info").child("phone").getValue(String.class);
                    name = snapshot.child("info").child("name").getValue(String.class);
                    if(phone != null){
                        binding.tvPhoneAdmin.setText(phone);
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                    if (url != null) {
                        Log.d("ChatFragment", "LOI");
                        Glide.with(requireContext())
                                .load(url).placeholder(R.drawable.logotoyshop).into(binding.imageAvatarAdmin);
                        binding.swipeRefreshLayout.setRefreshing(false);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchInfoAdmin(OnAdminInfoFetchedListener listener) {
        DatabaseReference infoAdminRef = FirebaseDatabase.getInstance().getReference("account");
        Query query = infoAdminRef.orderByChild("role").equalTo("admin");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    if (userId != null) {
                        listener.onAdminInfoFetched(userId);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchAvatarAdmin(userId_admin);

    }

    private interface OnAdminInfoFetchedListener {
        void onAdminInfoFetched(String userId);
    }
}
