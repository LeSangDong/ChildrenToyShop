package com.example.toysshop.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.activitys.ConfirmOrderActivity;
import com.example.toysshop.adapter.CartAdapter;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.configs.CenterGridItemDecoration;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.FragmentCartBinding;
import com.example.toysshop.listener.OnCartItemChangeListener;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Toy;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements OnCartItemChangeListener {
    private static final String TAG = "CartFragment";

    private FragmentCartBinding binding;

    private CartAdapter cartAdapter;
    private CartDao cartDao;
    private FirebaseAuth auth;
    private NavController navController;
    private List<CartModel> cartItems;
    private List<Toy> mListToy;
    private ToyAdapter toyAdapter;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent.class)) {
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent.class);
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(UpdateCartEvent event) {
        loadCartItems();
        countCartItem();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iNit(view);
        CartDatabase cartDatabase = CartDatabase.getInstance(getContext());
        cartDao = cartDatabase.cartDao();
//        Log.d(TAG, "Shimmer animation started.");
        binding.shimmerFacebook.startShimmerAnimation();
        countCartItem();

        loadCartItems();


//        binding.scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                // Lấy vị trí cuộn hiện tại của ScrollView
//                int scrollY = binding.scrollview.getScrollY();
//
//                // Làm cái gì đó dựa trên vị trí cuộn (scrollY)
//                if (scrollY > 0) {
//                    binding.layoutBottom.setVisibility(View.GONE);
//
//                } else {
//
//                    binding.layoutBottom.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        binding.ivBack.setOnClickListener(v -> {
            navController.navigateUp();

        });

        binding.btnOrder.setOnClickListener(v->{
            new Thread(() -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                List<CartModel> checkedCartItems = cartDao.getCheckedCartItems(userId);


                double totalPrice = 0;
                for (CartModel item : checkedCartItems) {
                    totalPrice += item.getPrice() * item.getQuantity();
                }

                // Chuyển danh sách item đã được chọn qua OrderActivity
                if(checkedCartItems.size() > 0){
                    Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
                    intent.putParcelableArrayListExtra("checkedCartItems", new ArrayList<>(checkedCartItems));
                    intent.putExtra("totalPrice", totalPrice);
                    startActivity(intent);
                }
                else{
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show()
                    );
                }

            }).start();

        });


    }


    private void countCartItem() {
        new Thread(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                int itemCount = cartDao.getCartItemCount(userId);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (itemCount > 0) {
                        binding.tvNotification.setText(String.valueOf(itemCount));
                        binding.scrollview.setVisibility(View.VISIBLE);
                        binding.layoutBottom.setVisibility(View.VISIBLE);
                        binding.layoutEmptyCart.setVisibility(View.GONE);
                        binding.layoutSuggestProduct.setVisibility(View.GONE);
                    } else {
                        binding.tvNotification.setVisibility(View.GONE);
                        binding.scrollview.setVisibility(View.GONE);
                        binding.layoutBottom.setVisibility(View.GONE);
                        binding.layoutEmptyCart.setVisibility(View.VISIBLE);
                        binding.layoutSuggestProduct.setVisibility(View.VISIBLE);
                        fetchAllProduct();
                    }
                });

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
                   binding.shimmerFacebook.stopShimmerAnimation();
                   binding.shimmerFacebook.setVisibility(View.GONE);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_All_Toy", error.getMessage());




            }
        });

    }

    private void loadCartItems() {

        new Thread(() -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            List<CartModel> cartItems = cartDao.getCartItems(userId);
            getActivity().runOnUiThread(() -> {
                cartAdapter = new CartAdapter(cartItems, this);
                binding.recyclerview.setAdapter(cartAdapter);

            });
        }).start();
    }


    private void iNit(View view) {
        navController = Navigation.findNavController(view);
        auth = FirebaseAuth.getInstance();
        CartDatabase cartDatabase = CartDatabase.getInstance(getContext());
        cartDao = cartDatabase.cartDao();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        //set up recyclerview suggest toy
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerviewSuggestProduct.setHasFixedSize(true);
        binding.recyclerviewSuggestProduct.addItemDecoration(new CenterGridItemDecoration(spacing));
        binding.recyclerviewSuggestProduct.setLayoutManager(new GridLayoutManager(
                requireContext(), 2));

    }


    @Override
    public void onCartItemChanged(double totalPrice) {
        updateTotalPrice(totalPrice);


    }


    @Override
    public void onItemSelectedCountChanged(int count) {
        updateSelectedItemCount(count);


    }

    private void updateSelectedItemCount(int count) {
        binding.tvCountItemCheck.setText(new StringBuilder().append("(").append(count).append(")"));
    }

    private void updateTotalPrice(double totalPrice) {
        DecimalFormat format = new DecimalFormat("#,###");
        binding.tvPrice.setText(new StringBuilder().append(format.format(totalPrice)).append("đ"));
        if(totalPrice > 0){
            binding.tvPriceDelivery.setText(new StringBuilder().append(format.format(22000)).append("đ"));
            double sumPrice = 22000 + totalPrice;
            binding.tvPriceSum.setText(new StringBuilder().append(format.format(sumPrice)).append("đ"));
        }
        else{
            binding.tvPriceDelivery.setText(new StringBuilder().append(format.format(0)).append("đ"));
            binding.tvPriceSum.setText(new StringBuilder().append(format.format(0)).append("đ"));
        }

    }


}