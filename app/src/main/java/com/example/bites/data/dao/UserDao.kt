package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long // Returns the new rowId

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE userID = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM user WHERE Mail = :email")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>
}