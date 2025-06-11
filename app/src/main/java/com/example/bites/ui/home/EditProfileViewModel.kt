package com.example.bites.ui.profile

import android.app.Application
import android.app.usage.UsageEvents
import android.util.Log
import androidx.lifecycle.*
import com.example.bites.data.AppDatabase
import com.example.bites.data.entity.AddressEntity // New
import com.example.bites.data.entity.UserEntity
import com.example.bites.data.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.bites.util.Event
import com.example.bites.util.AppConstants

class EditProfileViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {

    private val userRepository: UserRepository

    // User fields
    val firstName = savedStateHandle.getLiveData("firstName", "")
    val lastName = savedStateHandle.getLiveData("lastName", "")
    val email = savedStateHandle.getLiveData("email", "")
    val phoneNumber = savedStateHandle.getLiveData("phoneNumber", "")
    val birthDate = savedStateHandle.getLiveData("birthDate", "")


    // Address fields
    val street = savedStateHandle.getLiveData("street", "")
    val city = savedStateHandle.getLiveData("city", "")
    val country = savedStateHandle.getLiveData("country", "")
    val postalCode = savedStateHandle.getLiveData("postalCode", "")
    val buildingName = savedStateHandle.getLiveData("buildingName", "")
    val floorNumber = savedStateHandle.getLiveData("floorNumber", "")
    val doorNumber = savedStateHandle.getLiveData("doorNumber", "")
    val additionalInfo = savedStateHandle.getLiveData("additionalInfo", "")


    private val _saveResult = MutableLiveData<Event<Boolean>>()
    val saveResult: LiveData<Event<Boolean>> get() = _saveResult

    private var currentLoadedUserId: Long? = null
    private var currentLoadedAddressId: Long? = null // To preserve ID of loaded address

    init {
        val appDb = AppDatabase.getInstance(application)
        userRepository = UserRepository(appDb.userDao(), appDb.addressDao())
        loadCurrentUserAndAddress()
    }

    private fun loadCurrentUserAndAddress() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().firstOrNull()
            user?.let {
                currentLoadedUserId = it.userID
                if (firstName.value.isNullOrEmpty()) firstName.postValue(it.firstName)
                // ... (load other user fields as before) ...
                if (lastName.value.isNullOrEmpty()) lastName.postValue(it.lastName)
                if (email.value.isNullOrEmpty()) email.postValue(it.mail)
                if (phoneNumber.value.isNullOrEmpty()) phoneNumber.postValue(it.phoneNumber)
                if (birthDate.value.isNullOrEmpty()) birthDate.postValue(it.birthDate)
            }

            val address = userRepository.getCurrentUserPrimaryAddress().firstOrNull()
            address?.let {
                currentLoadedAddressId = it.addressID
                if (street.value.isNullOrEmpty()) street.postValue(it.street)
                if (city.value.isNullOrEmpty()) city.postValue(it.city)
                if (country.value.isNullOrEmpty()) country.postValue(it.country)
                if (postalCode.value.isNullOrEmpty()) postalCode.postValue(it.postalCode)
                if (buildingName.value.isNullOrEmpty()) buildingName.postValue(it.buildingName)
                if (floorNumber.value.isNullOrEmpty()) floorNumber.postValue(it.floorNumber)
                if (doorNumber.value.isNullOrEmpty()) doorNumber.postValue(it.doorNumber)
                if (additionalInfo.value.isNullOrEmpty()) additionalInfo.postValue(it.additionalInformation)
            }
        }
    }

    fun saveProfile() {
        if (firstName.value.isNullOrBlank() || lastName.value.isNullOrBlank() || email.value.isNullOrBlank()) {
            _saveResult.value = Event(false)
            return
        }
        // Basic validation for core address fields (optional, expand as needed)
        if (street.value.isNullOrBlank() || city.value.isNullOrBlank() || country.value.isNullOrBlank() || postalCode.value.isNullOrBlank()) {
            // Consider how to handle if user clears address fields - delete address or save blanks?
            // For now, let's assume if they are editing, they intend to save what's there.
            // If you require an address, add specific validation here.
        }


        viewModelScope.launch {
            // Save User
            val userToSave = UserEntity(
                userID = currentLoadedUserId ?: 0L, // Repository handles setting CURRENT_USER_ID if 0L for new user
                firstName = firstName.value!!,
                lastName = lastName.value!!,
                mail = email.value!!,
                phoneNumber = phoneNumber.value ?: "",
                birthDate = birthDate.value ?: ""
            )
            userRepository.saveUserProfile(userToSave)

            // Save Address (create new or update existing)
            // We need a valid userID, which should be AppConstants.CURRENT_USER_ID after user is saved or if it already exists
            val userIDForAddress = currentLoadedUserId ?: AppConstants.CURRENT_USER_ID

            val addressToSave = AddressEntity(
                addressID = currentLoadedAddressId ?: 0L, // 0L means new address
                userID = userIDForAddress, // Link to the current user
                street = street.value ?: "",
                city = city.value ?: "",
                country = country.value ?: "",
                postalCode = postalCode.value ?: "",
                buildingName = buildingName.value,
                floorNumber = floorNumber.value,
                doorNumber = doorNumber.value,
                additionalInformation = additionalInfo.value
            )
            // Only save address if some core fields are filled (optional logic)
            if (addressToSave.street.isNotBlank() || addressToSave.city.isNotBlank() ||
                addressToSave.country.isNotBlank() || addressToSave.postalCode.isNotBlank()) {
                userRepository.saveUserAddress(addressToSave)
            } else if (currentLoadedAddressId != null && currentLoadedAddressId != 0L) {
                // If all main fields are blank but an address existed, you might want to delete it.
                // For simplicity, we'll just not save it if all core fields are blank.
                // Or, your saveUserAddress could be modified to delete if fields are blank.
                Log.d("EditProfileVM", "Core address fields are blank, not saving/updating address.")
            }


            _saveResult.value = Event(true)
        }
    }
}