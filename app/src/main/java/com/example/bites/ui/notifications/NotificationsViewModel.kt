package com.example.bites.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "No active deliveries yet. Start by going to the dashboard!"
    }
    val text: LiveData<String> = _text
}