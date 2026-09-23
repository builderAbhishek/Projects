package com.ais.swaadpe.data.local.dao

import androidx.room.*
import com.ais.swaadpe.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartEntity)

    @Update
    suspend fun updateItem(item: CartEntity)

    @Query("UPDATE cart_items SET price = :newPrice WHERE id = :itemId")
    suspend fun updateItemPrice(itemId: Int, newPrice: Double)

    @Delete
    suspend fun deleteItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteById(itemId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getTotalPrice(): Flow<Double?>
}
