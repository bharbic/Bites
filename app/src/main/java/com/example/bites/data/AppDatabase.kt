package com.example.bites.data // Or your main data package

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bites.data.dao.AddressDao
import com.example.bites.data.dao.CouponDao
import com.example.bites.data.dao.CourierDao
import com.example.bites.data.dao.MenuItemDao
import com.example.bites.data.dao.OrderDao
import com.example.bites.data.dao.OrderItemDao
import com.example.bites.data.dao.ShopDao
import com.example.bites.data.dao.UserDao
import com.example.bites.data.entity.AddressEntity
import com.example.bites.data.entity.CouponEntity
import com.example.bites.data.entity.CourierEntity
import com.example.bites.data.entity.MenuItemEntity
import com.example.bites.data.entity.OrderEntity
import com.example.bites.data.entity.OrderItemEntity
import com.example.bites.data.entity.ShopEntity
import com.example.bites.data.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AddressEntity::class,
        ShopEntity::class,
        MenuItemEntity::class,
        CourierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CouponEntity::class
    ],
    version = 1, // Start with version 1 since you are pre-populating from an asset
    exportSchema = false // Set to true if you want to export schema to a JSON file for version control (good practice for complex migrations later)
)
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods for each DAO. Room will generate the implementation.
    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun shopDao(): ShopDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun courierDao(): CourierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun couponDao(): CouponDao

    companion object {
        @Volatile // Ensures that writes to this field are immediately made visible to other threads
        private var INSTANCE: AppDatabase? = null

        // Replace with the actual name of your database file on the device.
        // This is what Room will name the copied/managed database.
        private const val DATABASE_NAME = "deliverydb.db"

        // Replace with the exact name of your SQLite file in the 'app/src/main/assets/' folder.
        private const val ASSET_DB_PATH = "deliverydb.db" // <<<--- IMPORTANT: SET THIS CORRECTLY

        fun getInstance(context: Context): AppDatabase {
            // Double-checked locking to ensure thread safety for singleton instantiation
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // This is the crucial line for pre-populating your database from the assets folder.
                    // Room copies this file (if it doesn't already exist or if schema validation fails and there's no migration).
                    .createFromAsset(ASSET_DB_PATH)
                    // If you later need to update the schema AFTER the initial pre-population:
                    // 1. Update your Entities.
                    // 2. Increment the 'version' in @Database.
                    // 3. Provide Migration objects here using .addMigrations(MIGRATION_1_2, MIGRATION_2_3, ...)
                    //    if you want to preserve user data from the previous version.
                    //
                    // Alternatively, for development, or if losing data on schema change is acceptable:
                    // .fallbackToDestructiveMigration()
                    // This will delete the existing database and re-create it from the asset if a migration path isn't found.
                    // Use with caution in production as it WIPES ALL LOCAL DATA in the database.
                    .build()
                INSTANCE = instance
                instance // Return the newly created instance
            }
        }
    }
}