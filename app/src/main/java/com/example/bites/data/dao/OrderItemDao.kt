package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>)

    @Query("SELECT * FROM OrderItems WHERE orderID = :orderId")
    fun getItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>
}