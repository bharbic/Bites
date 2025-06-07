package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("SELECT * FROM address WHERE addressID = :id")
    fun getAddressById(id: Long): Flow<AddressEntity?>

    @Query("SELECT * FROM address WHERE userID = :userId")
    fun getAddressesForUser(userId: Long): Flow<List<AddressEntity>>
}