package com.example.bites.data.entity // Or your preferred package

import android.os.Parcelable
import com.example.bites.data.entity.MenuItemEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartDisplayItem(
    val menuItem: MenuItemEntity,
    var quantity: Int
) : Parcelable {
    val itemTotalPrice: Double
        get() = menuItem.price * quantity.toDouble() // Ensure double arithmetic
}