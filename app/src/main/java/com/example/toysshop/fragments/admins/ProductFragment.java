package com.example.toysshop.fragments.admins;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toysshop.R;
import com.example.toysshop.activitys.AddProductActivity;
import com.example.toysshop.adapter.ProductAdminAdapter;
import com.example.toysshop.databinding.FragmentProductBinding;
import com.example.toysshop.model.Toy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private FirebaseAuth auth;

    private ProductAdminAdapter productAdminAdapter;
    private List<Toy> mList;

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

        //action buttonm
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
        mList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Toy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot productSnapshot : snapshot.getChildren()){
                                Toy toy = productSnapshot.getValue(Toy.class);
                                if(toy != null){
                                    mList.add(toy);
                                }
                            }
                            productAdminAdapter = new ProductAdminAdapter(mList);
                            binding.recyclerview.setAdapter(productAdminAdapter);

                        }
                        else{
                            binding.tvNoProduct.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void initialized() {
        auth = FirebaseAuth.getInstance();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

    }
}