package com.example.toysshop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.toysshop.R;
import com.example.toysshop.activitys.SeenDetailOrderedActivity;
import com.example.toysshop.databinding.RowItemOrderAdminBinding;
import com.example.toysshop.model.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminAdapter.ViewHolder> {
    List<Order> mListOrder;
    Context context;
    DatabaseReference ordersRef;

    public OrderAdminAdapter(List<Order> mListOrder) {

        this.mListOrder = mListOrder;
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
    }

    @NonNull
    @Override
    public OrderAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemOrderAdminBinding binding = RowItemOrderAdminBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminAdapter.ViewHolder holder, int position) {
        context = holder.itemView.getContext();
        Order order = mListOrder.get(position);

        Glide.with(context)
                .load(order.getAvatar_user())
                .placeholder(R.drawable.imag_user_default)
                .into(holder.binding.imgAvatar);

        holder.binding.tvNameUser.setText(order.getName_user());
        holder.binding.tvOrderId.setText(order.getOrderId());
        holder.binding.tvPhone.setText(formatPhoneNumber(order.getPhone()));
        holder.binding.tvAddress.setText(order.getAddress());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        holder.binding.tvSum.setText(new StringBuilder().append(decimalFormat.format(order.getTotalPrice())).append(" VND"));

        int totalQuantity = 0;
        for (int i = 0; i < order.getCartItems().size(); i++) {
            totalQuantity += order.getCartItems().get(i).getQuantity();
        }
        holder.binding.tvQuantity.setText(String.valueOf(totalQuantity));

        holder.binding.tvDay.setText(order.getOrderDate());

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, SeenDetailOrderedActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(order.getCartItems()));
            context.startActivity(intent);

        });
        boolean isShipped = "đang vận chuyển".equals(order.getStatus());
        holder.binding.check.setChecked(isShipped);
        holder.binding.check.setEnabled(!isShipped);

        holder.binding.check.setOnClickListener(v->{
            if(holder.binding.check.isChecked()){
                DatabaseReference orderRef = ordersRef.child(order.getUserId()).child(order.getOrderId());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                orderRef.child("status").setValue("đang vận chuyển");
                orderRef.child("time_deny").setValue(currentTime)
                        .addOnSuccessListener(aVoid->{
                          //  Toast.makeText(context, "Order status updated to 'đang vận chuyển'", Toast.LENGTH_SHORT).show();
                            holder.binding.check.setEnabled(false);
                        })
                        .addOnFailureListener(error->{
                            Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show();
                            holder.binding.check.setChecked(false);

                        });
            }
        });

        holder.binding.ivDelete.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Lý do từ chối đơn hàng");
            final EditText input = new EditText(context);
            builder.setView(input);

            builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                String reason = input.getText().toString();
                if (!reason.isEmpty()) {
                    // Update the order with the deletion reason and status
                    DatabaseReference orderRef = ordersRef.child(order.getUserId()).child(order.getOrderId());
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    orderRef.child("status").setValue("đã bị từ chối");
                    orderRef.child("message_reason").setValue(reason);
                    orderRef.child("time_deny").setValue(currentTime)
                            .addOnSuccessListener(aVoid -> {
                                //Toast.makeText(context, "Order has been deleted with reason", Toast.LENGTH_SHORT).show();
                                mListOrder.remove(position);
                                notifyItemRemoved(position);
                            })
                            .addOnFailureListener(error -> {
                                Toast.makeText(context, "Failed to delete order", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(context, "Lý do không được để trống", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

            builder.show();


        });
    }

    @Override
    public int getItemCount() {
        return mListOrder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowItemOrderAdminBinding binding;

        public ViewHolder(@NonNull RowItemOrderAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


        public static String formatPhoneNumber(String phoneNumber) {
            if (phoneNumber.startsWith("+84")) {
                return "0" + phoneNumber.substring(3);
            }
            return phoneNumber;
        }

}
