package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.example.toysshop.R;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.databinding.ActivityShowProductBinding;
import com.example.toysshop.model.Toy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowProductActivity extends AppCompatActivity {
    private ActivityShowProductBinding binding;
    private ToyAdapter toyAdapter;
    private List<Toy> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set background statusbar
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        //init
        iNit();
        //set view
       int category_id = getIntent().getIntExtra("category_id",-1);
       if(category_id != -1){
           showProduct(category_id);
       }





    }

    private void showProduct(int categoryId) {
        mProductList = new ArrayList<>();
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Toy");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot productSnapshot : snapshot.getChildren()){
                        Toy toy = productSnapshot.getValue(Toy.class);
                        if(toy != null && toy.getCategoryId() == categoryId){
                            mProductList.add(toy);
                        }
                    }
                    toyAdapter = new ToyAdapter(mProductList);
                    binding.recyclerview.setAdapter(toyAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void iNit() {
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new GridLayoutManager(ShowProductActivity.this,2));
    }

    }
