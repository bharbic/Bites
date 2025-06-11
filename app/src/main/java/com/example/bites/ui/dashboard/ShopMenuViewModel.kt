package com.example.bites.ui.dashboard

import android.app.Application
import android.util.Log // *** ADDED: Import Log for debugging ***
import androidx.lifecycle.*
import com.example.bites.data.AppDatabase
import com.example.bites.data.dao.MenuItemDao // *** ADDED: Import your DAO ***  // *** ADDED: Assuming you have AppDatabase, or your DI method ***
import com.example.bites.data.entity.MenuItemEntity
import com.example.bites.data.entity.CartDisplayItem
import kotlinx.coroutines.launch // *** ADDED: Import launch for coroutines ***

class ShopMenuViewModel(
    application: Application,
    // *** MODIFIED: Expect Int for currentShopId as per our discussion for DAO compatibility ***
    private val currentShopId: Int
) : AndroidViewModel(application) {

    // *** --- ADDED SECTION: For loading menu items from DAO --- ***
    private val menuItemDao: MenuItemDao

    private val _menuItemsForShop = MutableLiveData<List<MenuItemEntity>>()
    val menuItemsForShop: LiveData<List<MenuItemEntity>> = _menuItemsForShop // This is what the Fragment will observe
    // *** --- END OF ADDED SECTION --- ***

    // Your existing cart related LiveData (was _itemsInCart, I'll rename for clarity if you wish, or keep as is)
    // For consistency with my previous examples, let's call it _itemsInCartList
    private val _itemsInCartList = MutableLiveData<List<MenuItemEntity>>(emptyList())
    // Keep this if other parts of ShopMenuFragment observe it directly by MenuItemEntity

    // This part was good, using _itemsInCart (now _itemsInCartList)
    val displayableCartItems: LiveData<List<CartDisplayItem>> = _itemsInCartList.map { menuItems ->
        menuItems.groupBy { it.itemID }
            .map { (_, items) ->
                CartDisplayItem(menuItem = items.first(), quantity = items.size)
            }
    }

    // *** ADDED: init block to initialize DAO and load menu items ***
    init {
        // Get an instance of the DAO.
        // THIS IS A SIMPLIFIED WAY. In a real app, use dependency injection (Hilt, Koin)
        // or pass the DAO through the factory if not using DI.
        menuItemDao = AppDatabase.getInstance(application).menuItemDao()
        loadShopMenuItems()
    }

    // *** ADDED: Function to load menu items ***
    private fun loadShopMenuItems() {
        viewModelScope.launch {
            try {
                // Use the DAO method that expects an Int
                menuItemDao.getAvailableMenuItemsForShop(currentShopId).collect { items ->
                    _menuItemsForShop.value = items
                    Log.d("ShopMenuViewModel", "Loaded menu items for shop $currentShopId: ${items.size} items.")
                }
            } catch (e: Exception) {
                Log.e("ShopMenuViewModel", "Error loading menu items for shop $currentShopId", e)
                _menuItemsForShop.value = emptyList() // Post empty list on error to prevent nulls
            }
        }
    }

    // Your existing cart logic - now operates on _itemsInCartList
    fun addItemToCart(menuItem: MenuItemEntity) {
        val currentList = _itemsInCartList.value?.toMutableList() ?: mutableListOf()
        currentList.add(menuItem)
        _itemsInCartList.value = currentList
        Log.d("ShopMenuViewModel", "Added to cart: ${menuItem.name}. Cart size: ${currentList.size}")
    }

    fun removeItemFromCart(menuItem: MenuItemEntity) { // Example, if you had one
        val currentList = _itemsInCartList.value?.toMutableList() ?: mutableListOf()
        if (currentList.remove(menuItem)) { // Check if removal was successful
            _itemsInCartList.value = currentList
            Log.d("ShopMenuViewModel", "Removed from cart: ${menuItem.name}. Cart size: ${currentList.size}")
        } else {
            Log.w("ShopMenuViewModel", "Attempted to remove item not in cart: ${menuItem.name}")
        }
    }

    val cartSubtotal: LiveData<Double> = displayableCartItems.map { cartDisplayItems ->
        cartDisplayItems.sumOf { it.itemTotalPrice }
    }

    val deliveryCost: Double = 3.00 // Keep this or make it dynamic

    val cartTotal: LiveData<Double> = cartSubtotal.map { subtotal ->
        if (subtotal > 0) {
            subtotal + deliveryCost
        } else {
            0.0
        }
    }

    fun getCurrentCartForNavigation(): List<CartDisplayItem>? {
        return displayableCartItems.value
    }
}