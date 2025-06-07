package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "address",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userID"],
            childColumns = ["userID"],
            onDelete = ForeignKey.NO_ACTION, // Default. Change if needed (e.g., CASCADE)
            onUpdate = ForeignKey.NO_ACTION  // Default
        )
    ]
)
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "addressID")
    val addressID: Long = 0,

    @ColumnInfo(name = "userID", index = true) // Index for foreign key is good practice
    val userID: Long,

    @ColumnInfo(name = "Country")
    val country: String,

    @ColumnInfo(name = "City")
    val city: String,

    @ColumnInfo(name = "Street")
    val street: String,

    @ColumnInfo(name = "PostalCode")
    val postalCode: String,

    @ColumnInfo(name = "BuildingName")
    val buildingName: String?,

    @ColumnInfo(name = "FloorNumber")
    val floorNumber: String?,

    @ColumnInfo(name = "DoorNumber")
    val doorNumber: String?,

    @ColumnInfo(name = "AdditionalInformation")
    val additionalInformation: String?
)