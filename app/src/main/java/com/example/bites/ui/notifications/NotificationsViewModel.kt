package com.example.bites.ui.notifications // Or your chosen package for this ViewModel

import android.util.Log // Add Log import
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.bites.data.entity.CartDisplayItem // Ensure this import is correct

class NotificationsViewModel : ViewModel() {

    val deliveryCost: Double = 3.00

    private val _cartItems = MutableLiveData<List<CartDisplayItem>>(emptyList())
    val cartItems: LiveData<List<CartDisplayItem>> = _cartItems

    fun initializeCart(items: List<CartDisplayItem>) {
        // Use toMutableList to ensure the list is modifiable if needed,
        // or ensure operations always create new lists.
        // For simple assignment like this from NavArgs, it's usually fine.
        if (_cartItems.value != items) { // Avoid unnecessary updates if list is identical
            _cartItems.value = items
            Log.d("NotificationsVM", "Cart initialized/updated. Count: ${items.size}")
        }
    }

    val cartSubtotal: LiveData<Double> = _cartItems.map { items ->
        items.sumOf { cartItem: CartDisplayItem -> // Explicitly type 'cartItem' (which was 'it')
            cartItem.itemTotalPrice
        }
    }

    val cartTotal: LiveData<Double> = cartSubtotal.map { subtotal ->
        if (subtotal > 0 || (_cartItems.value?.isNotEmpty() == true && subtotal == 0.0)) { // Add delivery if cart has items, even if subtotal is 0 (e.g. free items)
            subtotal + deliveryCost
        } else {
            0.0
        }
    }

    // ***** NEW METHOD TO REMOVE AN ITEM *****
    fun removeItemFromCart(itemToRemove: CartDisplayItem) {
        val currentItems = _cartItems.value?.toMutableList() // Create a mutable copy
        if (currentItems != null) {
            // Data class 'equals' will compare menuItem and quantity.
            // This is usually what you want for removing a specific cart entry.
            val removed = currentItems.remove(itemToRemove)
            if (removed) {
                _cartItems.value = currentItems // Update LiveData to trigger UI refresh
                Log.d("NotificationsVM", "${itemToRemove.menuItem.name} (Qty: ${itemToRemove.quantity}) removed. New count: ${currentItems.size}")
            } else {
                // This might happen if the item was already removed or something changed.
                Log.w("NotificationsVM", "Failed to remove ${itemToRemove.menuItem.name}. Item not found as specified.")
            }
        } else {
            Log.w("NotificationsVM", "Attempted to remove item from a null or empty cart list.")
        }
        // Subtotal and Total will recalculate automatically due to being transformations of _cartItems.
    }

    // Default text LiveData
    private val _text = MutableLiveData<String>().apply {
        value = "Review Your Cart"
    }
    val text: LiveData<String> = _text
}