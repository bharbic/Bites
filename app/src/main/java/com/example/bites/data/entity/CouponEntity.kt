package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Coupons",
    indices = [Index(value = ["Code"], unique = true)]
)
data class CouponEntity(
    @PrimaryKey // Not auto-generated
    @ColumnInfo(name = "couponID")
    val couponID: Int,

    @ColumnInfo(name = "Code")
    val code: String,

    @ColumnInfo(name = "Discount")
    val discount: Double, // Percentage

    @ColumnInfo(name = "ExpirationDate")
    val expirationDate: String, // Assuming ISO8601 String. Consider TypeConverter.

    @ColumnInfo(name = "MinOrderAmount", defaultValue = "0")
    val minOrderAmount: Double
)