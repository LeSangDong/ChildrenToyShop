package com.example.toysshop.untils;

public class OrderCountEvent {
    private int orderCount;

    public OrderCountEvent(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
