package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItemEntity)

    @Update
    suspend fun updateMenuItem(menuItem: MenuItemEntity)

    @Query("SELECT * FROM menu_item WHERE itemID = :id")
    fun getMenuItemById(id: Int): Flow<MenuItemEntity?>

    @Query("SELECT * FROM menu_item WHERE shopID = :shopId")
    fun getMenuItemsForShop(shopId: String): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_item WHERE shopID = :shopId AND Availability = 1")
    fun getAvailableMenuItemsForShop(shopId: Int): Flow<List<MenuItemEntity>>
}