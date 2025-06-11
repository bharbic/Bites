package com.example.bites.data.repository

import com.example.bites.data.dao.AddressDao // New
import com.example.bites.data.dao.UserDao
import com.example.bites.data.entity.AddressEntity // New
import com.example.bites.data.entity.UserEntity
import com.example.bites.util.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map // To get the first address

class UserRepository(
    private val userDao: UserDao,
    private val addressDao: AddressDao // Add AddressDao
) {

    fun getCurrentUser(): Flow<UserEntity?> {
        return userDao.getUserById(AppConstants.CURRENT_USER_ID)
    }

    // Get the first address for the current user
    fun getCurrentUserPrimaryAddress(): Flow<AddressEntity?> {
        return addressDao.getAddressesForUser(AppConstants.CURRENT_USER_ID)
            .map { addresses -> addresses.firstOrNull() } // Take the first address, or null if none
    }

    suspend fun saveUserProfile(user: UserEntity) {
        val userToSave = if (user.userID == 0L) {
            user.copy(userID = AppConstants.CURRENT_USER_ID)
        } else {
            user
        }
        userDao.insertUser(userToSave)
    }

    // Save or update an address for the current user
    suspend fun saveUserAddress(address: AddressEntity) {
        // Ensure the address has the correct userID
        val addressToSave = if (address.userID != AppConstants.CURRENT_USER_ID) {
            address.copy(userID = AppConstants.CURRENT_USER_ID)
        } else {
            address
        }
        // If addressID is 0, it's a new address, insert it.
        // If addressID is not 0, it's an existing address, update it (REPLACE handles this).
        addressDao.insertAddress(addressToSave)
    }


    suspend fun createDefaultUserAndAddressIfNone() {
        val existingUser = userDao.getUserById(AppConstants.CURRENT_USER_ID).firstOrNull()
        if (existingUser == null) {
            val defaultUser = UserEntity(
                userID = AppConstants.CURRENT_USER_ID,
                firstName = "Your",
                lastName = "Name",
                phoneNumber = "0000000000",
                mail = "youremail@example.com",
                birthDate = "YYYY-MM-DD"
            )
            userDao.insertUser(defaultUser)

            // Also create a default address
            val existingAddress = addressDao.getAddressesForUser(AppConstants.CURRENT_USER_ID).firstOrNull()
            if (existingAddress == null) {
                val defaultAddress = AddressEntity(
                    userID = AppConstants.CURRENT_USER_ID,
                    country = "Default Country",
                    city = "Default City",
                    street = "123 Default St",
                    postalCode = "00000",
                    buildingName = null,
                    floorNumber = null,
                    doorNumber = null,
                    additionalInformation = null
                )
                addressDao.insertAddress(defaultAddress)
            }
        } else {
            // User exists, check if address exists
            val existingAddress = addressDao.getAddressesForUser(AppConstants.CURRENT_USER_ID).firstOrNull()
            if (existingAddress == null) {
                val defaultAddress = AddressEntity(
                    userID = AppConstants.CURRENT_USER_ID,
                    country = "Default Country",
                    city = "Default City",
                    street = "123 Default St",
                    postalCode = "00000",
                    addressID = TODO(),
                    buildingName = TODO(),
                    floorNumber = TODO(),
                    doorNumber = TODO(),
                    additionalInformation = TODO()
                )
                addressDao.insertAddress(defaultAddress)
            }
        }
    }
}