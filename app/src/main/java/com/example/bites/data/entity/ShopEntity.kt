package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Shops",
    indices = [
        Index(value = ["PhoneNumber"], unique = true),
        Index(value = ["Email"], unique = true)
    ]
)
data class ShopEntity(
    @PrimaryKey // Not auto-generated based on schema
    @ColumnInfo(name = "shopID")
    val shopID: Int, // Assuming this ID comes from an external source or is predefined

    @ColumnInfo(name = "Name")
    val name: String,

    @ColumnInfo(name = "Type")
    val type: String, // You'd validate ('Grocery', 'Restaurant', etc.) in business logic

    @ColumnInfo(name = "PhoneNumber")
    val phoneNumber: String,

    @ColumnInfo(name = "Email")
    val email: String,

    @ColumnInfo(name = "Address")
    val address: String
)