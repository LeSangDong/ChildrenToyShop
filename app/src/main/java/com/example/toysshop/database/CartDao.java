package com.example.toysshop.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.toysshop.model.CartModel;

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insertCart(CartModel cartModel);

    @Update
    void updateCart(CartModel cartModel);

    @Query("UPDATE cart_tb SET quantity = :quantity WHERE id = :cartItemId AND userId = :userId")
    void updateCartItemQuantity(int cartItemId, String userId, int quantity);

    @Query("UPDATE cart_tb SET ischecked = :isChecked WHERE id = :id AND userId = :userId")
    void updateCartItemCheckedStatus(int id, String userId, boolean isChecked);

    @Query("DELETE FROM cart_tb WHERE id=:id AND userId=:userId")
    void deleteCartById(int id,String userId);

    @Query("SELECT * FROM cart_tb WHERE userId = :userId")
    List<CartModel> getCartItems(String userId);


    @Query("SELECT * FROM cart_tb WHERE userId = :userId AND productId = :productId LIMIT 1")
    CartModel getCartItemByUserAndProduct(String userId, int productId);

    @Query("SELECT COUNT(*) FROM cart_tb WHERE userId = :userId")
    int getCartItemCount(String userId);

    @Query("SELECT * FROM cart_tb WHERE userId = :userId AND ischecked = 1")
    List<CartModel> getCheckedCartItems(String userId);
    @Query("DELETE FROM cart_tb WHERE userId = :userId AND isChecked = 1")
    void deleteCheckedCartItems(String userId);





}
