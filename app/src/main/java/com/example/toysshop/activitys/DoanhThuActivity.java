package com.example.toysshop.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.adapter.DoanhThuAdapter;
import com.example.toysshop.databinding.ActivityDoanhThuBinding;
import com.example.toysshop.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoanhThuActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityDoanhThuBinding binding;
    private DoanhThuAdapter doanhThuAdapter;
    private List<Order> orderedList;
    private double totalRevenue = 0;
    private TextView _startDay, _endDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoanhThuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iNit();
        loadAllDoanhThu();
        setupDatePickers();
        binding.iconSearch.setOnClickListener(v -> filterDoanhThuByDateRange());
        binding.btnBack.setOnClickListener(v->{
            finish();
        });

        binding.iconDelete.setOnClickListener(v->{
            showDialogDelete();
        });
    }

    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắn muốn xóa toàn bộ hóa đơn ?");
        builder.setPositiveButton("Xóa", (dialog, i) -> {

            dialog.dismiss();


        });
        builder.setNegativeButton("Hủy", (dialog, i) -> {
            dialog.dismiss();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupDatePickers() {
        binding.edtStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.edtStartDay);
            }
        });

        binding.edtEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(binding.edtEndDay);
            }
        });
    }

    private void showDatePickerDialog(final TextView dateEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(DoanhThuActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1; // tháng trong DatePicker bắt đầu từ 0
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month, year);
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void filterDoanhThuByDateRange() {
        String startDateStr = binding.edtStartDay.getText().toString();
        String endDateStr = binding.edtEndDay.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khoảng thời gian hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Order> filteredList = new ArrayList<>();
            double filteredTotalRevenue = 0;

            if (orderedList != null) {
                for (Order order : orderedList) {
                    String orderDateString = order.getOrderDate(); // Lấy ngày tháng dưới dạng String từ đối tượng Order
                    Date orderDate = dateFormat.parse(orderDateString); // Chuyển đổi String thành Date

                    if (orderDate != null && !orderDate.before(startDate) && !orderDate.after(endDate)) {
                        filteredList.add(order);
                        filteredTotalRevenue += order.getTotalPrice();
                    }
                }
            }
            if(filteredList.isEmpty()){
                binding.tvNoBill.setVisibility(View.VISIBLE);
            }

            doanhThuAdapter = new DoanhThuAdapter(filteredList);
            binding.recylerview.setAdapter(doanhThuAdapter);
            DecimalFormat format = new DecimalFormat("#,###");
            binding.tvTotal.setText(new StringBuilder().append(format.format(filteredTotalRevenue)).append(" VND"));

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadAllDoanhThu() {
        orderedList = new ArrayList<>();
        DatabaseReference oderedRef = FirebaseDatabase.getInstance().getReference("Orders");
        oderedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderedList.clear();
                totalRevenue = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && "đang vận chuyển".equals(order.getStatus())) {
                            orderedList.add(order);
                            totalRevenue += order.getTotalPrice();
                        }
                    }
                }
                Collections.sort(orderedList, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        try {
                            Date date1 = dateFormat.parse(o1.getOrderDate());
                            Date date2 = dateFormat.parse(o2.getOrderDate());
                            return date2.compareTo(date1); // Sắp xếp giảm dần theo ngày
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                if(orderedList.isEmpty()){
                    binding.tvNoBill.setVisibility(View.VISIBLE);
                }

                doanhThuAdapter = new DoanhThuAdapter(orderedList);
                binding.recylerview.setAdapter(doanhThuAdapter);
                DecimalFormat format = new DecimalFormat("#,###");
                binding.tvTotal.setText(new StringBuilder().append(format.format(totalRevenue)).append(" VND"));
                binding.swipeRefreshLayout.setRefreshing(false);

                Log.d("DoanhThuActivity", "Total Orders Loaded: " + orderedList.size());
                Log.d("DoanhThuActivity", "Total Revenue: " + totalRevenue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.swipeRefreshLayout.setRefreshing(false);
                Log.e("DoanhThuActivity", "Database Error: " + error.getMessage());
            }
        });
    }

    private void iNit() {
        _startDay = findViewById(R.id.edt_start_day);
        _endDay = findViewById(R.id.edt_end_day);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setRefreshing(true);
        binding.recylerview.setHasFixedSize(true);
        binding.recylerview.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onRefresh() {
        loadAllDoanhThu();
        binding.edtStartDay.setText("");
        binding.edtEndDay.setText("");
        binding.tvNoBill.setVisibility(View.GONE);
    }
}
