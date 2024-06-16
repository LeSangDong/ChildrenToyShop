package com.example.toysshop.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toysshop.database.CartDao;
import com.example.toysshop.database.CartDatabase;
import com.example.toysshop.databinding.RowItemCartBinding;
import com.example.toysshop.listener.OnCartItemChangeListener;
import com.example.toysshop.model.CartModel;
import com.example.toysshop.untils.UpdateCartEvent;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<CartModel> cartItems;
    private int currentQuantity = 1;
    private CartDao cartDao;
    private CartDatabase cartDatabase;
    private FirebaseAuth auth;

    private OnCartItemChangeListener listener;

    public CartAdapter(List<CartModel> cartItems,OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
        cartDatabase = CartDatabase.getInstance(context);
        cartDao = cartDatabase.cartDao();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      RowItemCartBinding binding = RowItemCartBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        CartModel cartModel = cartItems.get(position);
        context = holder.itemView.getContext();
        holder.binding.tvNameProduct.setText(cartModel.getName_product());
        holder.binding.checkbox.setChecked(cartModel.isIschecked());
        Glide.with(context).load(cartModel.getImgUrl()).into(holder.binding.imageView);
        DecimalFormat format = new DecimalFormat("#,###");
        holder.binding.tvPriceProduct.setText(new StringBuilder("Giá: ").append(format.format(cartModel.getPrice())).append("đ"));
        holder.binding.tvQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        holder.binding.checkbox.setChecked(cartModel.isIschecked());
        holder.binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartModel.setIschecked(isChecked);
            updateCheckedStatus(cartModel);
            // Gọi phương thức tính tổng giá từ CartFragment
            caculatedSelectItemCount();
           calculateSelectedTotalPrice();
        });

        caculatedSelectItemCount();
        calculateSelectedTotalPrice();

        //action minus
        holder.binding.ivMinus.setOnClickListener(v->minusQuantity(cartModel, holder));
        //action plus
        holder.binding.ivPlus.setOnClickListener(v->plusQuantity(cartModel,holder));



    }

    private void updateCheckedStatus(CartModel cartModel) {
        new Thread(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                cartDao.updateCartItemCheckedStatus(cartModel.getId(), currentUser.getUid(), cartModel.isIschecked());

            }
        }).start();
    }

    private void caculatedSelectItemCount() {
        int count = 0;
        for(CartModel item : cartItems){
            if(item.isIschecked()){
                count++;
            }
        }
        if (listener != null) {
            listener.onItemSelectedCountChanged(count);
        }
    }

    private void calculateSelectedTotalPrice() {
        double totalPrice = 0.0;
        for (CartModel item : cartItems) {
            if (item.isIschecked()) { // Kiểm tra xem sản phẩm này có được chọn không
                totalPrice += item.getQuantity() * item.getPrice();
            }
        }
        if (listener != null) {
            listener.onCartItemChanged(totalPrice);
        }

    }

//    private void updateTotalPrice(ViewHolder holder, CartModel cartModel) {
//
//        double totalPrice = 0.0;
//        for (CartModel item : cartItems) {
//            totalPrice += item.getQuantity() * item.getPrice();
//        }
//        if (listener != null) {
//            listener.onCartItemChanged(totalPrice);
//        }
//
//    }

    private void plusQuantity(CartModel cartModel, ViewHolder holder) {
        int newQuantity = cartModel.getQuantity() + 1;
        cartModel.setQuantity(newQuantity);
        updateCartItem(cartModel, newQuantity, holder);
        calculateSelectedTotalPrice();


    }

    private void updateCartItem(CartModel cartModel, int newQuantity, ViewHolder holder) {
        new Thread(()->{
            FirebaseUser currentUser = auth.getCurrentUser();
            if(currentUser != null){
                cartDao.updateCartItemQuantity(cartModel.getId(),currentUser.getUid(),newQuantity);
                ((Activity) context).runOnUiThread(() -> holder.binding.tvQuantity.setText(new StringBuilder().append(newQuantity)));
            }
            else{
                ((Activity) context).runOnUiThread(() -> Toast.makeText(context, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show());
            }

        }).start();
    }

    private void minusQuantity(CartModel cartModel, ViewHolder holder) {
        if(cartModel.getQuantity() > 1){
            int newQuantity = cartModel.getQuantity() - 1;
            cartModel.setQuantity(newQuantity);
            updateCartItem(cartModel, newQuantity, holder);
            calculateSelectedTotalPrice();
        }
        else{
            openDialogDelete(cartModel);
            
        }
    }

    private void openDialogDelete(CartModel cartModel) {
        Log.d("CartAdapter", "openDialogDelete called");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn xóa sản phẩm này không?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if(currentUser != null){
                new Thread(()->{
                    Log.d("CartAdapter", "Deleting cart item...");
                    cartDao.deleteCartById(cartModel.getId(),currentUser.getUid());
                    cartItems.remove(cartModel);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            caculatedSelectItemCount();
                            calculateSelectedTotalPrice();
                            notifyDataSetChanged(); // Cập nhật RecyclerView
                            Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new UpdateCartEvent());
                        }
                    });
                    Log.d("CartAdapter", "Cart item deleted successfully");
                    // Hiển thị thông báo hoặc cập nhật giao diện người dùng khác tùy thuộc vào yêu cầu của bạn

                }).start();


            }
            else{
                Toast.makeText(context,"Bạn chưa đăng nhập!",Toast.LENGTH_SHORT).show();
            }
           dialog.dismiss();


        });
        builder.setNegativeButton("Không",(dialog,which)->{
            dialog.dismiss();

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RowItemCartBinding binding;

        public ViewHolder(@NonNull RowItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
