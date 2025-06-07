package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.CourierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourierDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourier(courier: CourierEntity)

    @Query("SELECT * FROM courier WHERE courierID = :id")
    fun getCourierById(id: Int): Flow<CourierEntity?>

    @Query("SELECT * FROM courier")
    fun getAllCouriers(): Flow<List<CourierEntity>>
}