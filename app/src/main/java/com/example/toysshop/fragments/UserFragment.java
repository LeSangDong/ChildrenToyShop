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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toysshop.R;
import com.example.toysshop.activitys.GetStartedActivity;
import com.example.toysshop.activitys.LoginActivity;
import com.example.toysshop.activitys.MainActivity;
import com.example.toysshop.activitys.SettingActivity;
import com.example.toysshop.activitys.WaitConfirmOrderActivity;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.configs.CenterGridItemDecoration;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.FragmentUserBinding;
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
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private FirebaseAuth auth;
    private CartDao cartDao;
    private NavController navController;
    private ToyAdapter toyAdapter;
    private List<Toy> mListToy;



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
        binding.ivCart1.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), WaitConfirmOrderActivity.class));
        });

        //fetch all product
        fetchAllProduct();

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

    }
}