package com.example.toysshop.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.DeliveryOrderActivity;
import com.example.toysshop.activitys.GetStartedActivity;
import com.example.toysshop.activitys.LoginActivity;
import com.example.toysshop.activitys.MainActivity;
import com.example.toysshop.activitys.SettingActivity;
import com.example.toysshop.activitys.ViewInfoActivity;
import com.example.toysshop.activitys.ViewNotificationActivity;
import com.example.toysshop.activitys.WaitConfirmOrderActivity;
import com.example.toysshop.adapter.ProductFeedBackAdapter;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.configs.CenterGridItemDecoration;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.FragmentUserBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Order;
import com.example.toysshop.model.Toy;
import com.example.toysshop.untils.UpdateCartEvent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class UserFragment extends Fragment{

    private FragmentUserBinding binding;
    private FirebaseAuth auth;
    private CartDao cartDao;
    private NavController navController;
    private ToyAdapter toyAdapter;
    private List<Toy> mListToy;
    private List<Order> mListOrderProcess;
    private List<Order> mListOrderDelivery;
    private List<CartModel> mListProductFeedBack;
    private ProductFeedBackAdapter productFeedBackAdapter;






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
      binding = FragmentUserBinding.inflate(inflater,container,false);
      return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iNit(view);

        setCollapsingToolbarTitle();
        CartDatabase cartDatabase = CartDatabase.getInstance(getContext());
        cartDao = cartDatabase.cartDao();
        countCartItem();

        binding.actionSetting.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), SettingActivity.class));

        });

        binding.fab.setOnClickListener(v->{
            navController.navigate(R.id.cartFragment);

        });
        binding.screenWaitConfirm.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), WaitConfirmOrderActivity.class));
        });
        binding.screenDelivery.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), DeliveryOrderActivity.class));
        });
        binding.iconNotification.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), ViewNotificationActivity.class));

        });

        //fetch all product
        fetchAllProduct();

        //fetch order process
        fetchOrderProcess();
        //fetch order delivery
        fetchOrderDelivery();

        //fetchavatar
        fetchAvatar();

        //fetchAllNotification
        fetchNotification();

        //fetchAllProductBought
        fetAllBougntProduct();





    }

    private void fetAllBougntProduct() {
        mListProductFeedBack = new ArrayList<>();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
            Query query = productRef.orderByChild("status").equalTo("đang vận chuyển");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && order.getStatus().equals("đang vận chuyển")) {
                            List<CartModel> cartItems = order.getCartItems();
                            if (cartItems != null) {
                                for (CartModel item : cartItems) {
                                    checkIfUserReviewedProduct(item, userId);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserFragment", "fetAllBougntProduct: onCancelled", error.toException());
                }
            });
        }
    }

    private void checkIfUserReviewedProduct(CartModel item, String userId) {

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Toy/" + item.getProductId() + "/reviews");
        reviewsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // User has not reviewed this product, add it to the list
                    mListProductFeedBack.add(item);
                    // Notify the adapter about the data change
                    if (productFeedBackAdapter == null) {
                        productFeedBackAdapter = new ProductFeedBackAdapter(mListProductFeedBack);
                        binding.recyclerviewFeedback.setAdapter(productFeedBackAdapter);
                    } else {
                        productFeedBackAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserFragment", "checkIfUserReviewedProduct: onCancelled", error.toException());
            }
        });
    }


    private void fetchNotification() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getUid());
            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int notificationCount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        if (order != null && order.getTime_deny() != null) {
                            notificationCount++;
                        }
                    }
                    if (notificationCount > 0) {
                        binding.tvNotification.setText(String.valueOf(notificationCount));
                        binding.tvNotification.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvNotification.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserFragment", "fetchNotifications: onCancelled", error.toException());
                }
            });
        } else {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchOrderDelivery() {
        mListOrderDelivery = new ArrayList<>();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference orderProcessRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getUid());
            orderProcessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mListOrderDelivery.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Order order = dataSnapshot.getValue(Order.class);
                        if (order != null && order.getStatus().equals("đang vận chuyển")) {
                            mListOrderDelivery.add(order);
                        }

                    }
                    displayOrderCount();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserFragment", "fetchOrderProcess: onCancelled", error.toException());

                }
            });

        }
        else{
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }


    }

    private void fetchAvatar() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account").child(userId).child("info");

            // Load avatar URL and user name
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(requireContext()).load(avatarUrl).into(binding.imgAvatar);
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchOrderProcess() {
        mListOrderProcess = new ArrayList<>();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference orderProcessRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getUid());
            orderProcessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mListOrderProcess.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Order order = dataSnapshot.getValue(Order.class);
                            if (order != null && order.getStatus().equals("Chờ xác nhận")) {
                                mListOrderProcess.add(order);
                            }

                    }
                    displayOrderCount();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UserFragment", "fetchOrderProcess: onCancelled", error.toException());

                }
            });

        }
        else{
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayOrderCount() {
        int orderCount = mListOrderProcess.size();
        int orderCountDelivery = mListOrderDelivery.size();
       if(orderCount > 0){
           binding.tvNotificationWait.setVisibility(View.VISIBLE);
           binding.tvNotificationWait.setText(String.valueOf(orderCount));
       }

        if(orderCountDelivery > 0){
            binding.tvNotificationDelivery.setText(String.valueOf(orderCountDelivery));
            binding.tvNotificationDelivery.setVisibility(View.VISIBLE);
        }


    }

    private void setCollapsingToolbarTitle() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
           FirebaseDatabase.getInstance().getReference("account")
                   .child(currentUser.getUid())
                   .child("info")
                   .addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.exists()){
                               String username = snapshot.child("name").getValue(String.class);
                               if(username != null){
                                   binding.collapsingToolbar.setTitle(username);
                               }
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
        }
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
                    binding.recyclerview.setAdapter(toyAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_All_Toy", error.getMessage());



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
                    binding.tvNotifiicationCart.setText(new StringBuilder().append(itemCount));
                }
                else{
                    binding.tvNotifiicationCart.setVisibility(View.GONE);
                }
            }


        }).start();
    }



    private void iNit(View view) {

        auth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);


        //setup recyclerview
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.addItemDecoration(new CenterGridItemDecoration(spacing));
        binding.recyclerview.setLayoutManager(new GridLayoutManager(
                requireContext(), 2));

        binding.recyclerviewFeedback.setHasFixedSize(true);
        binding.recyclerviewFeedback.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));

    }


}