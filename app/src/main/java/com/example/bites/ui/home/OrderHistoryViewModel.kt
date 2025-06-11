package com.example.bites.ui.home // Or a new package like ui.orderhistory

import android.app.Application
import androidx.lifecycle.*
import com.example.bites.data.entity.OrderEntity
import com.example.bites.data.repository.OrderRepository
import com.example.bites.util.AppConstants // Assuming you get current user ID from here
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository: OrderRepository

    private val currentUserId = AppConstants.CURRENT_USER_ID

    val userOrders: StateFlow<List<OrderEntity>>

    init {
        val database = com.example.bites.data.AppDatabase.getInstance(application)
        orderRepository = OrderRepository(database.orderDao(), database.orderItemDao())

        // Fetch orders for the current user.
        userOrders = if (currentUserId != 0L) { // Check if user ID is valid
            orderRepository.getOrdersForUser(currentUserId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        } else {
            // Handle case where user ID is not available (e.g., guest user)
            kotlinx.coroutines.flow.MutableStateFlow(emptyList<OrderEntity>()) // Emit empty list
                .asStateFlow()
        }
    }


}

// ViewModel Factory (if you don't have a generic one)
class OrderHistoryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderHistoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}