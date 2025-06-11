package com.example.bites.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.bites.data.AppDatabase
import com.example.bites.data.entity.AddressEntity
import com.example.bites.data.entity.UserEntity
import com.example.bites.data.repository.UserRepository
import com.example.bites.util.Event // Make sure you have this Event class
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository

    // For displaying some text (e.g., a title or message)
    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to Bites!" // Example initial value
    }
    val text: LiveData<String> = _text // Expose as LiveData

    // For user data
    val currentUser: LiveData<UserEntity?>
    val currentUserAddress: LiveData<AddressEntity?>

    // For navigation event
    private val _navigateToEditProfile = MutableLiveData<Event<Unit>>()
    val navigateToEditProfile: LiveData<Event<Unit>> = _navigateToEditProfile // Expose as LiveData

    init {
        val appDb = AppDatabase.getInstance(application)
        userRepository = UserRepository(appDb.userDao(), appDb.addressDao())
        currentUser = userRepository.getCurrentUser().asLiveData()
        currentUserAddress = userRepository.getCurrentUserPrimaryAddress().asLiveData()

        viewModelScope.launch {
            userRepository.createDefaultUserAndAddressIfNone()
        }
    }

    fun onEditProfileClicked() {
        _navigateToEditProfile.value = Event(Unit) // Trigger the event
    }
}