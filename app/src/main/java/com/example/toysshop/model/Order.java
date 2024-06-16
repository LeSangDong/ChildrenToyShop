package com.example.toysshop.model;

import java.util.List;

public class Order {
    String orderId;
    private String userId;
    private List<CartModel> cartItems;
    private double totalPrice;
    private String orderDate;
    private String deliveryDate;
    private String status;

    public Order() {
    }

    public Order(String orderId,String userId, List<CartModel> cartItems, double totalPrice,String orderDate, String deliveryDate, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartModel> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartModel> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


}
