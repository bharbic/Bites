package com.example.bites.ui.dashboard // Ensure this package is correct

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShopMenuViewModelFactory(
    private val application: Application,
    private val shopId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopMenuViewModel::class.java)) {
            // If the requested ViewModel is our ShopMenuViewModel,
            // create it with the application and shopId we have.
            return ShopMenuViewModel(application, shopId) as T
        }
        // If it's some other ViewModel, we don't know how to create it.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}