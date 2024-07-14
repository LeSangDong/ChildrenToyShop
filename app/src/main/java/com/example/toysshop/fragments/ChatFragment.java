package com.example.toysshop.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toysshop.R;
import com.example.toysshop.activitys.ChatActivity;
import com.example.toysshop.databinding.FragmentChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private String userId_admin;

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

        fetchInfoAdmin();


        binding.itemChatWithAdmin.setOnClickListener(v->{
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("chatWithUserId", userId_admin);
            getContext().startActivity(intent);
        });


    }

    private void fetchInfoAdmin() {
        DatabaseReference infoAdminRef = FirebaseDatabase.getInstance().getReference("account");
        Query query = infoAdminRef.orderByChild("role").equalTo("admin");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot userSnapshot: snapshot.getChildren()){
                   String userId = userSnapshot.getKey();
                   if(userId != null){
                       userId_admin = userId;
                   }
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}