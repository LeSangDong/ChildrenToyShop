package com.example.toysshop.activitys;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.toysshop.R;
import com.example.toysshop.adapter.ReviewAdapter;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.adapter.ViewPagerAdapter;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.ActivityDetailProductBinding;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.model.Review;
import com.example.toysshop.model.Toy;
import com.example.toysshop.untils.UpdateCartEvent;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.HashMap;
import java.util.List;

public class DetailProductActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityDetailProductBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Toy> mListToy;
    private ToyAdapter toyAdapter;
    private int product_id;
    private ArrayList<String> mListImage;
    private Toy toy;

    private FirebaseAuth auth;

    private HashMap<String, String> colorNameMap;

    private CartDao cartDao;




    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        //init
        initalized();

        CartDatabase cartDatabase = CartDatabase.getInstance(this);
        cartDao = cartDatabase.cartDao();
        countCartItem();

        toy = (Toy) getIntent().getSerializableExtra("toy");

        assert toy != null;
        product_id = toy.getId();

        mListImage = toy.getImageList();

        viewPagerAdapter = new ViewPagerAdapter(mListImage);
        binding.viewpagerProduct.setAdapter(viewPagerAdapter);

        binding.tvSumImage.setText(new StringBuilder().append("/ ").append(mListImage.size()));
        binding.tvIndexImage.setText(new StringBuilder().append(1));

        binding.viewpagerProduct.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tvIndexImage.setText(new StringBuilder().append(position + 1));
            }
        });

        //set view
        binding.tvNameProduct.setText(toy.getTitle());
        DecimalFormat format = new DecimalFormat("#,###");
        binding.tvPriceProduct.setText(new StringBuilder().append(format.format(toy.getPrice())).append("đ"));
        binding.tvDescription.setText(toy.getDescription());
       if(toy.getStar()>0){
           binding.ratingBar.setRating((float) toy.getStar());
       }
       else{
           binding.ratingBar.setVisibility(View.GONE);
       }
        binding.tvPercent.setText(new StringBuilder("Giảm ").append(toy.getPriceDiscount()).append("%"));
        if (toy.getPriceDiscount() > 0) {
            double discountedPrice = toy.getPrice() * (1 - toy.getPriceDiscount() / 100.0);
            binding.tvOldPrice.setPaintFlags(binding.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvOldPrice.setText(new StringBuilder().append(format.format(toy.getPrice())).append("đ"));

            binding.tvPriceProduct.setText(new StringBuilder().append(format.format(discountedPrice)).append("đ"));
        } else {
            binding.tvPercent.setVisibility(View.GONE);
            binding.tvOldPrice.setVisibility(View.GONE);
        }
        binding.tvSizeTable.setText(new StringBuilder().append(toy.getSize_product()));
        binding.tvTrademarkTable.setText(new StringBuilder().append(toy.getTrademark()));
        binding.tvColorTable.setText(new StringBuilder().append(toy.getColor_product()));
        //fetchAllProduc
        fetchAllProduct();

        binding.btnBack.setOnClickListener(v -> {
            startActivity(new Intent(DetailProductActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

        binding.ivCart.setOnClickListener(v->{
            Intent intent = new Intent(DetailProductActivity.this, MainActivity.class);
            intent.putExtra("gotocart",1);
            startActivity(intent);
            finish();


        });

        binding.iconGetAddress.setOnClickListener(v->{
            startActivity(new Intent(this, HomeAddressActivity.class));
        });


        fetchAddress();
        fetchReview(product_id);





        //action add to cart

        binding.btnAddToCart.setOnClickListener(v -> {

            addtoCart(toy);

        });
    }

    private void fetchReview(int productId) {
        DatabaseReference toyRef = FirebaseDatabase.getInstance().getReference("Toy")
                .child(String.valueOf(productId)).child("reviews");
        toyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   List<Review> reviews = new ArrayList<>();
                   for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                       Review review = reviewSnapshot.getValue(Review.class);
                       if (review != null) {
                           fetchUserInfo(review, reviews);
                       }
                   }
               }
               else{
                   binding.tvNoUserReview.setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Fetch_Review", "Failed to fetch reviews: " + error.getMessage());

            }
        });
    }

    private void fetchUserInfo(Review review, List<Review> reviews) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("account")
                .child(review.getUserId()).child("info");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userName = snapshot.child("name").getValue(String.class);
                    String userImage = snapshot.child("avatarUrl").getValue(String.class);

                    review.setUserName(userName);
                    review.setUserImage(userImage);
                    reviews.add(review);

                    updateReviewRecyclerView(reviews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Fetch_User_Info", "Failed to fetch user info: " + error.getMessage());

            }
        });
    }

    private void updateReviewRecyclerView(List<Review> reviews) {
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
        binding.recyclerviewReview.setAdapter(reviewAdapter);

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
                            binding.tvAddressDelivery.setText(address);
                        }
                        else{
                            binding.tvAddressDelivery.setText(new StringBuilder().append("Chưa có địa chỉ"));
                        }

                    binding.swipeRefreshLayout.setRefreshing(false);

                    }
                    else{
                        binding.tvAddressDelivery.setText(new StringBuilder().append("Chưa có địa chỉ"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else{
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }


    }

    private void countCartItem() {

            new Thread(() ->{
                FirebaseUser currentUser = auth.getCurrentUser();
                if(currentUser != null){
                    String userId = currentUser.getUid();
                    int itemCount = cartDao.getCartItemCount(userId);
                    runOnUiThread(() -> {
                        if(itemCount > 0){

                            binding.tvNotification.setText(new StringBuilder().append(itemCount));
                            binding.tvNotification.setVisibility(View.VISIBLE);
                        }
                        else{
                            binding.tvNotification.setVisibility(View.GONE);
                        }
                    });
                }


            }).start();


    }

    private void addtoCart(Toy toy) {

        String userId = auth.getCurrentUser().getUid(); // Lấy userId từ FirebaseAuth

        new Thread(() -> {
            CartModel existingCartItem = cartDao.getCartItemByUserAndProduct(userId, toy.getId());

            if (existingCartItem != null) {
                // Cập nhật số lượng và tổng giá
                existingCartItem.setQuantity(existingCartItem.getQuantity() + 1); // Tăng số lượng lên 1
                existingCartItem.setTotalPrice(existingCartItem.getTotalPrice() + toy.getPrice());
                cartDao.updateCart(existingCartItem);
            } else {
                // Thêm mới
                CartModel newCartItem = new CartModel(userId, toy.getId(), toy.getImageList().get(0), toy.getTitle(), (toy.getPrice() * (1 - toy.getPriceDiscount() / 100.0)), 1, toy.getPrice(), false);
                cartDao.insertCart(newCartItem);


            }
            EventBus.getDefault().postSticky(new UpdateCartEvent());

            runOnUiThread(()->

              //  Toast.makeText(DetailProductActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show());

            {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green));
                Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View customView = inflater.inflate(R.layout.snackbar_success_layout, null);
                snackbarLayout.addView(customView, 0);
                snackbar.show();

            });
        }).start();

    }




    private void initalized() {
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);
        auth = FirebaseAuth.getInstance();
        binding.recyclerviewSuggestProduct.setHasFixedSize(true);
        binding.recyclerviewSuggestProduct.setLayoutManager(new GridLayoutManager(this, 2));

        binding.recyclerviewReview.setHasFixedSize(true);
        binding.recyclerviewReview.setLayoutManager(new LinearLayoutManager(this));


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
                        if (toy != null && toy.getId() != product_id) {
                            mListToy.add(toy);
                        }
                    }
                    toyAdapter = new ToyAdapter(mListToy);
                    binding.recyclerviewSuggestProduct.setAdapter(toyAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Log_All_Toy", error.getMessage());

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onRefresh() {
        fetchAddress();

    }
}
