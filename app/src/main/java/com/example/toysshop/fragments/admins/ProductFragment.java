package com.example.toysshop.fragments.admins;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toysshop.R;
import com.example.toysshop.activitys.AddProductActivity;
import com.example.toysshop.databinding.FragmentProductBinding;
import com.google.firebase.auth.FirebaseAuth;


public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentProductBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init
        initialized();

        //action button
        actionButton();

        //fetchAllProduct
        fetchAllProduct();


    }

    private void actionButton() {
        binding.btnAdd.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), AddProductActivity.class));
        });
    }

    private void fetchAllProduct() {

    }

    private void initialized() {
        auth = FirebaseAuth.getInstance();
    }
}