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
    private String avatar_user;
    private String name_user;
    private String phone;
    private String address;
    private String message_reason;
    private String time_deny;


    public Order() {
    }

    public Order(String orderId,String userId, List<CartModel> cartItems, double totalPrice,String orderDate, String deliveryDate, String status,String avatar_user,String phone,String name_user, String address) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.avatar_user = avatar_user;
        this.phone = phone;
        this.name_user = name_user;
        this.address = address;

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


    public String getAvatar_user() {
        return avatar_user;
    }

    public void setAvatar_user(String avatar_user) {
        this.avatar_user = avatar_user;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage_reason() {
        return message_reason;
    }

    public void setMessage_reason(String message_reason) {
        this.message_reason = message_reason;
    }

    public String getTime_deny() {
        return time_deny;
    }

    public void setTime_deny(String time_deny) {
        this.time_deny = time_deny;
    }
}
