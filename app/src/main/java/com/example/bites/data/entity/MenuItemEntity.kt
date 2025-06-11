package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable // Import Parcelable
import kotlinx.parcelize.Parcelize // Import Parcelize

@Parcelize // Add this annotation
@Entity(
    tableName = "menu_item",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class, // Make sure ShopEntity is defined and accessible
            parentColumns = ["shopID"],
            childColumns = ["shopID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MenuItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "itemID")
    val itemID: Int,

    @ColumnInfo(name = "shopID", index = true)
    val shopID: Int,

    @ColumnInfo(name = "Name")
    val name: String,

    @ColumnInfo(name = "Description")
    val description: String?,

    @ColumnInfo(name = "Price")
    val price: Double,

    @ColumnInfo(name = "Availability", defaultValue = "1")
    val availability: Boolean
) : Parcelable // Implement Parcelable