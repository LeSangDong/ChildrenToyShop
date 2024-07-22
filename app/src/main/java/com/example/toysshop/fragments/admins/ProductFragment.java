package com.example.toysshop.fragments.admins;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toysshop.R;
import com.example.toysshop.activitys.AddProductActivity;
import com.example.toysshop.adapter.ProductAdminAdapter;
import com.example.toysshop.databinding.FragmentProductBinding;
import com.example.toysshop.model.Toy;
import com.example.toysshop.untils.OrderCountEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private FirebaseAuth auth;

    private ProductAdminAdapter productAdminAdapter;
    private List<Toy> mList;
    private List<Toy> mListSearch;
    private NavController navController;

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
       binding.tvNotification.setText(new StringBuilder().append(orderCount));
    }

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
        initialized(view);

        //action buttonm
        actionButton();

        //fetchAllProduct
        fetchAllProduct();
        binding.iconNotification.setOnClickListener(v->{
            navController.navigate(R.id.orderFragment);

        });
        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = binding.searchView.getText().toString().trim();
                loadListProduct(query);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void loadListProduct(String query) {
        mListSearch.clear();
        for(Toy toy: mList){
            if(toy.getTitle().toLowerCase().contains(query.toLowerCase())){
                mListSearch.add(toy);
            }

        }
        productAdminAdapter = new ProductAdminAdapter(mListSearch);
        binding.recyclerview.setAdapter(productAdminAdapter);

    }

    private void actionButton() {
        binding.btnAdd.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), AddProductActivity.class));
        });
    }

    private void fetchAllProduct() {
        mList = new ArrayList<>();
        mListSearch = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Toy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            for(DataSnapshot productSnapshot : snapshot.getChildren()){
                                Toy toy = productSnapshot.getValue(Toy.class);
                                if(toy != null){
                                    mList.add(toy);
                                    mListSearch.add(toy);
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

    private void initialized(View view) {
        navController = Navigation.findNavController(view);
        auth = FirebaseAuth.getInstance();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

    }
}