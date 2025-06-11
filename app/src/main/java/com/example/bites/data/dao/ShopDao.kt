package com.example.bites.data.dao // Or your DAO package

import androidx.lifecycle.LiveData // Only if you decide to return LiveData directly from DAO
import androidx.room.Dao
import androidx.room.Query
import com.example.bites.data.entity.ShopEntity
import kotlinx.coroutines.flow.Flow // This is what we expect for .asLiveData()

@Dao
interface ShopDao {

    // ... any other DAO methods you have (insert, update, delete, etc.)

    @Query("SELECT * FROM Shops")
    fun getAllShops(): Flow<List<ShopEntity>> // << this is what you have

    // ALTERNATIVE (if you preferred LiveData directly from DAO, though Flow is often more flexible):
    // @Query("SELECT * FROM Shops")
    // fun getAllShops(): LiveData<List<ShopEntity>>
}