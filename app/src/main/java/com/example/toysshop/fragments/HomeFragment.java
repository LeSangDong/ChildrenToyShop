package com.example.toysshop.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.toysshop.R;
import com.example.toysshop.activitys.AddressActivity;
import com.example.toysshop.activitys.HomeAddressActivity;
import com.example.toysshop.activitys.MapsActivity;
import com.example.toysshop.adapter.BannerViewpagerAdapter;
import com.example.toysshop.adapter.CategoryAdapter;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.configs.CenterGridItemDecoration;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.FragmentHomeBinding;
import com.example.toysshop.model.Category;
import com.example.toysshop.model.Toy;
import com.example.toysshop.untils.UpdateCartEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<Category> mCategorys;
    private List<String> mUrlBanners;
    private BannerViewpagerAdapter bannerViewpagerAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Toy> mbestToy;
    private List<Toy> mnewToy;
    private List<Toy> mlikeToy;

    private List<Toy> mListToy;
    private ToyAdapter toyAdapter;
    private FirebaseAuth auth;

    private CartDao cartDao;
    private NavController navController;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent.class)) {
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent.class);
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(UpdateCartEvent event){
        countCartItem();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }






    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iNit(view);

        CartDatabase cartDatabase = CartDatabase.getInstance(getContext());
        cartDao = cartDatabase.cartDao();
        countCartItem();



        //set dia chi
        binding.tvLocation.setPaintFlags(binding.tvLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvLocation.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), HomeAddressActivity.class));
        });
     showLoadingFragment();


     //fetch banner
        fetchBanner();
        //fetch category
        fetchCategory();
        //fetchNewToy
        fetchNewToy();
        //fetchBestToy
        fetchBestToy();
        //fetchLikeToy
        fetchLikeToy();
        //fetchAllProduct
        fetchAllProduct();

        fetchAddress();
        binding.ivCart.setOnClickListener(v->{

            navController.navigate(R.id.cartFragment);

        });

    }

    private void fetchAddress() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference phoneRef = FirebaseDatabase.getInstance().getReference("phone").child(currentUser.getUid());
            phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                            String address = snapshot.child("address").getValue(String.class);
                           if(address != null){
                               binding.tvLocation.setText(address);
                           }
                           else{
                               binding.tvTitleLocation.setVisibility(View.GONE);
                               binding.tvLocation.setVisibility(View.GONE);
                           }



                    }
                    else{
                        binding.tvTitleLocation.setVisibility(View.GONE);
                        binding.tvLocation.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else{
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }


    }

    private void fetchBanner() {
        mUrlBanners = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("banner")
                .child("url")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot urlSnapshot : snapshot.getChildren()){
                                String url = urlSnapshot.getValue(String.class);
                                if(url != null){
                                    mUrlBanners.add(url);
                                }

                            }
                            bannerViewpagerAdapter = new BannerViewpagerAdapter(mUrlBanners);
                            binding.viewpager2.setAdapter(bannerViewpagerAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void countCartItem() {

        new Thread(() ->{
            FirebaseUser currentUser = auth.getCurrentUser();
            if(currentUser != null){
                String userId = currentUser.getUid();
                int itemCount = cartDao.getCartItemCount(userId);
                if(itemCount > 0){
                    binding.tvNotification.setText(new StringBuilder().append(itemCount));
                }
                else{
                    binding.tvNotification.setVisibility(View.GONE);
                }
            }


        }).start();
    }



    private void fetchAllProduct() {
        mListToy = new ArrayList<>();
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Toy");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Toy toy = productSnapshot.getValue(Toy.class);
                        if (toy != null) {
                            mListToy.add(toy);
                        }
                    }
                    toyAdapter = new ToyAdapter(mListToy);
                    binding.recyclerviewSuggestProduct.setAdapter(toyAdapter);
                    hideLoadingFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_All_Toy", error.getMessage());
                hideLoadingFragment();

            }
        });

    }

    private void fetchLikeToy() {
        mlikeToy = new ArrayList<>();
        DatabaseReference bestToyRef = FirebaseDatabase.getInstance().getReference("Toy");
        Query query = bestToyRef.orderByChild("like").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot likeToySnapshot : snapshot.getChildren()) {
                        Toy toy = likeToySnapshot.getValue(Toy.class);
                        if (toy != null) {
                            mlikeToy.add(toy);
                        }
                    }
                    toyAdapter = new ToyAdapter(mlikeToy);
                    binding.recyclerviewLikeProduct.setAdapter(toyAdapter);
                    hideLoadingFragment();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_Like_Toy", error.getMessage());
                hideLoadingFragment();

            }
        });

    }

    private void fetchNewToy() {
        mnewToy = new ArrayList<>();
        DatabaseReference bestToyRef = FirebaseDatabase.getInstance().getReference("Toy");
        Query query = bestToyRef.orderByChild("newProduct").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot newToySnapshot : snapshot.getChildren()) {
                        Toy toy = newToySnapshot.getValue(Toy.class);
                        if (toy != null) {
                            mnewToy.add(toy);
                        }
                    }
                    toyAdapter = new ToyAdapter(mnewToy);
                    binding.recyclerviewNewProduct.setAdapter(toyAdapter);
                    hideLoadingFragment();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_New_Toy", error.getMessage());
                hideLoadingFragment();

            }
        });

    }

    private void fetchBestToy() {
        mbestToy = new ArrayList<>();
        DatabaseReference bestToyRef = FirebaseDatabase.getInstance().getReference("Toy");
        Query query = bestToyRef.orderByChild("bestToy").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot bestToySnapshot : snapshot.getChildren()) {
                        Toy toy = bestToySnapshot.getValue(Toy.class);
                        if (toy != null) {
                            mbestToy.add(toy);
                        }
                    }
                    toyAdapter = new ToyAdapter(mbestToy);
                    binding.recyclerviewBestProduct.setAdapter(toyAdapter);
                    hideLoadingFragment();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_Best_Toy", error.getMessage());
                hideLoadingFragment();

            }
        });
    }

    private void fetchCategory() {
        mCategorys = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Category")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                                Category category = categorySnapshot.getValue(Category.class);
                                if (category != null) {
                                    mCategorys.add(category);
                                }
                            }
                            categoryAdapter = new CategoryAdapter(mCategorys);
                            binding.recyclerviewCategory.setAdapter(categoryAdapter);
                            hideLoadingFragment();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Log_Category", error.getMessage());
                        hideLoadingFragment();

                    }
                });
    }


    private void iNit(View view) {

        auth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);


        //set up recyclerview category
        binding.recyclerviewCategory.setHasFixedSize(true);
        binding.recyclerviewCategory.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        //set up recyclerview best toy
        binding.recyclerviewBestProduct.setHasFixedSize(true);
        binding.recyclerviewBestProduct.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        //set up recyclerview is like toys
        binding.recyclerviewLikeProduct.setHasFixedSize(true);
        binding.recyclerviewLikeProduct.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        //set up recyclerview suggest toys
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerviewSuggestProduct.setHasFixedSize(true);
        binding.recyclerviewSuggestProduct.addItemDecoration(new CenterGridItemDecoration(spacing));
        binding.recyclerviewSuggestProduct.setLayoutManager(new GridLayoutManager(
                requireContext(), 2));
        //set up recyclerview new toys
        binding.recyclerviewNewProduct.setHasFixedSize(true);
        binding.recyclerviewNewProduct.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));

        //getCurrentLocation();


    }

    private void showLoadingFragment() {
        requireActivity().runOnUiThread(() -> {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Fragment existingFragment = getChildFragmentManager().findFragmentByTag("LOADING_FRAGMENT");
            if (existingFragment == null) {
                transaction.replace(R.id.fragment_container, new LoadingFragment(), "LOADING_FRAGMENT");
                transaction.commitAllowingStateLoss();
            }
        });
    }
    private void hideLoadingFragment() {
        requireActivity().runOnUiThread(() -> {
            Fragment existingFragment = getChildFragmentManager().findFragmentByTag("LOADING_FRAGMENT");
            if (existingFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .remove(existingFragment)
                        .commitAllowingStateLoss();
            }
        });
    }


}