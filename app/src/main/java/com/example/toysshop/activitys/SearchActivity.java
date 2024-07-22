package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.toysshop.R;

import com.example.toysshop.adapter.SearchHistoryAdapter;
import com.example.toysshop.adapter.ToyAdapter;
import com.example.toysshop.database.SearchHistoryDatabaseHelper;
import com.example.toysshop.databinding.ActivitySearchBinding;
import com.example.toysshop.listener.IActionSearchListener;
import com.example.toysshop.model.Toy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements IActionSearchListener {
    private ActivitySearchBinding binding;
    private SearchHistoryDatabaseHelper searchHistoryDatabaseHelper;
    private SearchHistoryAdapter searchHistoryAdapter;
    private ToyAdapter toyAdapter;
    private List<Toy> allProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchHistoryDatabaseHelper = new SearchHistoryDatabaseHelper(this);
        binding.btnBack.setOnClickListener(v -> finish());

        binding.iconSearch.setOnClickListener(v->{
            performSearch(binding.edtSearch.getText().toString().trim());
            String query = binding.edtSearch.getText().toString().trim();
            showSearchResults(query);

        });

        binding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    performSearch(binding.edtSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.recyclerviewProduct.setVisibility(View.GONE);
                binding.recyclerviewHistory.setVisibility(View.VISIBLE);

                loadSearchHistory();
                binding.tvDeleteAll.setVisibility(View.VISIBLE);
                if(binding.edtSearch.getText().toString().trim().isEmpty()){
                    binding.tvDeleteAll.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
       // loadSearchHistory();


        binding.tvDeleteAll.setOnClickListener(v->{
            searchHistoryDatabaseHelper.clearHistory();
            loadSearchHistory();
        });
        fetchAllProducts();
    }

    private void fetchAllProducts() {
        allProducts = new ArrayList<>();
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Toy");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Toy toy = productSnapshot.getValue(Toy.class);
                        if (toy != null) {
                            allProducts.add(toy);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void showSearchResults(String query) {
        List<Toy> filteredProducts = new ArrayList<>();
        for (Toy product : allProducts) {
            if (product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(product);
            }
        }

        toyAdapter = new ToyAdapter(filteredProducts);
        binding.recyclerviewProduct.setVisibility(View.VISIBLE);
        binding.recyclerviewHistory.setVisibility(View.GONE);
        binding.tvDeleteAll.setVisibility(View.GONE);
        binding.recyclerviewProduct.setHasFixedSize(true);
        binding.recyclerviewProduct.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerviewProduct.setAdapter(toyAdapter);
    }

    private void loadSearchHistory() {
        List<String> searchHistory = searchHistoryDatabaseHelper.getAllQueries();
        searchHistoryAdapter = new SearchHistoryAdapter(searchHistory,this);
        binding.recyclerviewHistory.setHasFixedSize(true);
        binding.recyclerviewHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewHistory.setAdapter(searchHistoryAdapter);
    }

    private void performSearch(String query) {
        if (!TextUtils.isEmpty(query)) {
            searchHistoryDatabaseHelper.insertQuery(query);
            loadSearchHistory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchHistoryDatabaseHelper.close();
    }

    @Override
    public void onReturn(String query) {
        binding.edtSearch.setText(query);
        binding.edtSearch.setSelection(query.length());

    }

    @Override
    public void onDeleteById(int position) {
        searchHistoryDatabaseHelper.deleteQuery(position + 1);
        loadSearchHistory();

    }
}
