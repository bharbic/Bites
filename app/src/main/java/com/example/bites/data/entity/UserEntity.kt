package com.example.bites.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userID")
    val userID: Long = 0, // Use Long for autoGenerate, provide default for data class

    @ColumnInfo(name = "FirstName")
    val firstName: String,

    @ColumnInfo(name = "LastName")
    val lastName: String,

    @ColumnInfo(name = "Phone_Number")
    val phoneNumber: String,

    @ColumnInfo(name = "Mail")
    val mail: String,

    @ColumnInfo(name = "Birth_Date")
    val birthDate: String // Assuming ISO8601 String for DATE. Consider TypeConverter for Date objects.
)