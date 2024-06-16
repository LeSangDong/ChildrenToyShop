package com.example.toysshop.listener;

public interface OnCartItemChangeListener {
    void onCartItemChanged(double totalPrice);
    void onItemSelectedCountChanged(int count);
}
