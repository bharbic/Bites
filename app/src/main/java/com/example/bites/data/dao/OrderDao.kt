package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE orderID = :id")
    fun getOrderById(id: Long): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE userID = :userId ORDER BY orderID DESC")
    fun getOrdersForUser(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE courierID = :courierId ORDER BY orderID DESC")
    fun getOrdersForCourier(courierId: Int): Flow<List<OrderEntity>>
}