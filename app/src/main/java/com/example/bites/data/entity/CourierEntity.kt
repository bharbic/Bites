package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courier",
    indices = [Index(value = ["PhoneNumber"], unique = true)]
)
data class CourierEntity(
    @PrimaryKey // Not auto-generated
    @ColumnInfo(name = "courierID")
    val courierID: Int,

    @ColumnInfo(name = "FirstName")
    val firstName: String,

    @ColumnInfo(name = "LastName")
    val lastName: String,

    @ColumnInfo(name = "PhoneNumber")
    val phoneNumber: String,

    @ColumnInfo(name = "VehicleType")
    val vehicleType: String // Validate ('Bike', 'Car', 'Motorcycle') in business logic
)