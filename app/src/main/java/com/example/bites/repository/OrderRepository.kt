package com.example.bites.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.bites.data.dao.OrderDao
import com.example.bites.data.dao.OrderItemDao
import com.example.bites.data.entity.OrderEntity
import com.example.bites.data.entity.OrderItemEntity

class OrderRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    /**
     * Creates a new order along with its items in a single transaction.
     *
     * @param order The OrderEntity to be inserted.
     * @param items The list of OrderItemEntity to be inserted for this order.
     * @return The ID of the newly created order, or -1 if an error occurred.
     */
    @WorkerThread
    suspend fun createOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Long {
        return try {
            // Insert the order and get its generated ID
            val newOrderId = orderDao.insertOrder(order)

            if (newOrderId > 0) {
                // Set the newOrderId for each item and insert them
                val itemsWithOrderId = items.map { it.copy(orderID = newOrderId) }
                orderItemDao.insertAllOrderItems(itemsWithOrderId)
                Log.d("OrderRepository", "Order created successfully with ID: $newOrderId and ${itemsWithOrderId.size} items.")
                newOrderId // Return the new order ID
            } else {
                Log.e("OrderRepository", "Failed to insert order. Received ID: $newOrderId")
                -1L // Indicate failure
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Exception while creating order with items", e)
            -1L // Indicate failure
        }
    }

    // --- Methods for Order History (We'll use these later) ---

    fun getOrdersForUser(userId: Long) = orderDao.getOrdersForUser(userId)

    fun getItemsForOrder(orderId: Long) = orderItemDao.getItemsForOrder(orderId)

}