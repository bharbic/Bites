package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "menu_item",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["shopID"],
            childColumns = ["shopID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MenuItemEntity(
    @PrimaryKey // Not auto-generated
    @ColumnInfo(name = "itemID")
    val itemID: Int,

    @ColumnInfo(name = "shopID", index = true)
    val shopID: Int,

    @ColumnInfo(name = "Name")
    val name: String,

    @ColumnInfo(name = "Description")
    val description: String?,

    @ColumnInfo(name = "Price")
    val price: Double, // REAL maps to Double

    @ColumnInfo(name = "Availability", defaultValue = "1")
    val availability: Boolean // Room handles Boolean to INT (0/1)
)