package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "OrderItems",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderID"],
            childColumns = ["orderID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MenuItemEntity::class,
            parentColumns = ["itemID"],
            childColumns = ["itemID"],
            onDelete = ForeignKey.CASCADE // Consider if menu item deletion should cascade here or if you want to keep order history
        )
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) // Assuming it should auto-generate
    @ColumnInfo(name = "orderItemID")
    val orderItemID: Long = 0,

    @ColumnInfo(name = "orderID", index = true)
    val orderID: Long, // Matches OrderEntity.orderID type

    @ColumnInfo(name = "itemID", index = true)
    val itemID: Int, // Matches MenuItemEntity.itemID type

    @ColumnInfo(name = "Quantity")
    val quantity: Int,

    @ColumnInfo(name = "Price")
    val price: Double // Price of the item at the time of order
)