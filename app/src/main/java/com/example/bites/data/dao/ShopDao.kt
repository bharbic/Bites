package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.ShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: ShopEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllShops(shops: List<ShopEntity>)

    @Update
    suspend fun updateShop(shop: ShopEntity)

    @Query("SELECT * FROM Shops WHERE shopID = :id")
    fun getShopById(id: Int): Flow<ShopEntity?>

    @Query("SELECT * FROM Shops")
    fun getAllShops(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM Shops WHERE Type = :type")
    fun getShopsByType(type: String): Flow<List<ShopEntity>>
}