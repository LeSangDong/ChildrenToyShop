package com.example.toysshop.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Toy implements Serializable {
    private boolean bestToy;
    private int categoryId;
    private String description;
    private String color_product;
    private String size_product;
    private int id;
    private ArrayList<String> imageList;
    private boolean newProduct;
    private double price;
    private int priceDiscount;
    private double star;
    private int count_feedback;
    private String title;
    private String trademark;
    private boolean isDiscount;
    private boolean isLike;

    public Toy() {
    }

    public String getColor_product() {
        return color_product;
    }

    public void setColor_product(String color_product) {
        this.color_product = color_product;
    }

    public String getSize_product() {
        return size_product;
    }

    public void setSize_product(String size_product) {
        this.size_product = size_product;
    }

    public boolean isBestToy() {
        return bestToy;
    }

    public void setBestToy(boolean bestToy) {
        this.bestToy = bestToy;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public boolean isNewProduct() {
        return newProduct;
    }

    public void setNewProduct(boolean newProduct) {
        this.newProduct = newProduct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(int priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrademark() {
        return trademark;
    }

    public void setTrademark(String trademark) {
        this.trademark = trademark;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        isDiscount = discount;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getCount_feedback() {
        return count_feedback;
    }

    public void setCount_feedback(int count_feedback) {
        this.count_feedback = count_feedback;
    }
}