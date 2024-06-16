package com.example.toysshop.untils;

import java.util.concurrent.atomic.AtomicLong;

public class OrderNumberGenerator {
    private static final AtomicLong sequence = new AtomicLong(1000000);
    public static String generateOrderNumber() {
        long number = sequence.incrementAndGet();
        return "_DH" + String.format("%07d", number); // Định dạng với 7 chữ số
    }
}
