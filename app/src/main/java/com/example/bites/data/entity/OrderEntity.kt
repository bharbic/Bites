package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.NO_ACTION // Or SET_NULL if appropriate and userID is nullable
        ),
        ForeignKey(
            entity = AddressEntity::class,
            parentColumns = ["addressID"],
            childColumns = ["addressID"],
            onDelete = ForeignKey.NO_ACTION // Or SET_NULL
        ),
        ForeignKey(
            entity = CourierEntity::class,
            parentColumns = ["courierID"],
            childColumns = ["courierID"],
            onDelete = ForeignKey.SET_NULL // If courier is deleted, set courierID in order to null
        )
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orderID")
    val orderID: Long = 0,

    @ColumnInfo(name = "userID", index = true)
    val userID: Long,

    @ColumnInfo(name = "addressID", index = true)
    val addressID: Long,

    @ColumnInfo(name = "courierID", index = true)
    val courierID: Int?, // Assuming it can be null and refers to CourierEntity.courierID

    @ColumnInfo(name = "Payment")
    val payment: String?,

    @ColumnInfo(name = "SpecialRequest")
    val specialRequest: String?,

    @ColumnInfo(name = "PaymentStatus", defaultValue = "Not paid")
    val paymentStatus: String, // Validate ('Paid', 'Pending', 'Not paid') in business logic

    @ColumnInfo(name = "DeliveryStatus", defaultValue = "Pending")
    val deliveryStatus: String, // Validate options in business logic

    @ColumnInfo(name = "Cutlery", defaultValue = "No")
    val cutlery: String // Validate ('Yes', 'No') in business logic (or use Boolean)
)