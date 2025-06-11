package com.example.bites.ui.notifications // Or your chosen package for this ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.bites.data.entity.CartDisplayItem // Ensure this import is correct

class NotificationsViewModel : ViewModel() {

    // You can make deliveryCost dynamic later if needed (e.g., based on distance or order value)
    val deliveryCost: Double = 3.00 // Example fixed delivery cost

    private val _cartItems = MutableLiveData<List<CartDisplayItem>>(emptyList())
    val cartItems: LiveData<List<CartDisplayItem>> = _cartItems

    /**
     * Initializes the cart with the items passed from the navigation arguments.
     * This should be called from NotificationsFragment.
     */
    fun initializeCart(items: List<CartDisplayItem>) {
        _cartItems.value = items
    }

    val cartSubtotal: LiveData<Double> = _cartItems.map { items ->
        items.sumOf { it.itemTotalPrice }
    }

    val cartTotal: LiveData<Double> = cartSubtotal.map { subtotal ->
        // Only add delivery cost if there's something in the cart
        if (subtotal > 0) {
            subtotal + deliveryCost
        } else {
            0.0
        }
    }

    // --- Optional: Functions to modify the cart (if you plan to add +/- buttons on this screen later) ---
    // Example:
    // fun increaseItemQuantity(itemId: Int) {
    //     val currentList = _cartItems.value?.toMutableList() ?: return
    //     val itemIndex = currentList.indexOfFirst { it.menuItem.itemID == itemId }
    //     if (itemIndex != -1) {
    //         currentList[itemIndex].quantity++
    //         _cartItems.value = currentList
    //     }
    // }
    //
    // fun decreaseItemQuantity(itemId: Int) {
    //     val currentList = _cartItems.value?.toMutableList() ?: return
    //     val itemIndex = currentList.indexOfFirst { it.menuItem.itemID == itemId }
    //     if (itemIndex != -1 && currentList[itemIndex].quantity > 1) { // Prevent going below 1
    //         currentList[itemIndex].quantity--
    //         _cartItems.value = currentList
    //     } else if (itemIndex != -1 && currentList[itemIndex].quantity == 1) {
    //         // Optional: remove item if quantity becomes 0 after decrease, or leave it to a separate remove button
    //         // removeItemFromCart(itemId)
    //     }
    // }
    //
    // fun removeItemFromCart(itemId: Int) {
    //     _cartItems.value = _cartItems.value?.filterNot { it.menuItem.itemID == itemId }
    // }
    // --- End Optional ---


    // This was part of the default ViewModel template, you can customize or remove it.
    private val _text = MutableLiveData<String>().apply {
        value = "Review Your Cart" // More appropriate title
    }
    val text: LiveData<String> = _text
}