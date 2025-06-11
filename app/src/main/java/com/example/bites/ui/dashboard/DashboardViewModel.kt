package com.example.bites.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel // Correct import
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.bites.data.AppDatabase
import com.example.bites.data.entity.ShopEntity

// Ensure your ShopDao.getAllShops() returns Flow<List<ShopEntity>>
// or if it returns LiveData directly, adjust the assignment below.

class DashboardViewModel(application: Application) : AndroidViewModel(application) { // Correctly extends AndroidViewModel

    private val shopDao = AppDatabase.getInstance(application).shopDao()

    val allShops: LiveData<List<ShopEntity>> = shopDao.getAllShops().asLiveData()
}