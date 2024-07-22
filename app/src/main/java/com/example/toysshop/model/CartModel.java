package com.example.toysshop.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_tb")
public class CartModel implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userId;
    private int productId;
    private String imgUrl;
    private String name_product;
    private Double price;
    private int quantity;
    private Double totalPrice;
    private boolean ischecked;


    public CartModel() {
    }

    public CartModel(String userId,int productId, String imgUrl, String name_product, Double price, int quantity, Double totalPrice, boolean ischecked) {
        this.userId = userId;
        this.productId = productId;
        this.imgUrl = imgUrl;
        this.name_product = name_product;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.ischecked = ischecked;
    }

    protected CartModel(Parcel in) {
        id = in.readInt();
        userId = in.readString();
        productId = in.readInt();
        imgUrl = in.readString();
        name_product = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        quantity = in.readInt();
        if (in.readByte() == 0) {
            totalPrice = null;
        } else {
            totalPrice = in.readDouble();
        }
        ischecked = in.readByte() != 0;
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName_product() {
        return name_product;
    }

    public void setName_product(String name_product) {
        this.name_product = name_product;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(userId);
        parcel.writeInt(productId);
        parcel.writeString(imgUrl);
        parcel.writeString(name_product);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
        parcel.writeInt(quantity);
        if (totalPrice == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(totalPrice);
        }
        parcel.writeByte((byte) (ischecked ? 1 : 0));
    }


}
