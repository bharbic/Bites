package com.example.bites.ui.notifications   // Or your chosen package

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.bites.data.AppDatabase
import com.example.bites.data.entity.AddressEntity
import com.example.bites.data.entity.CartDisplayItem // Your actual class
import com.example.bites.data.entity.OrderEntity
import com.example.bites.data.entity.OrderItemEntity
import com.example.bites.data.repository.OrderRepository
import com.example.bites.data.repository.UserRepository
import com.example.bites.util.AppConstants
import com.example.bites.util.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val orderRepository: OrderRepository
    private val userRepository: UserRepository

    private val _currentCartItems = MutableLiveData<List<CartDisplayItem>>()
    val currentCartItems: LiveData<List<CartDisplayItem>> = _currentCartItems

    private val _deliveryAddress = MutableLiveData<AddressEntity?>()
    val deliveryAddress: LiveData<AddressEntity?> = _deliveryAddress

    private val _currentUserId = MutableLiveData<Long>(AppConstants.CURRENT_USER_ID) // Example

    val deliveryAddressText: LiveData<String> = _deliveryAddress.map { addr ->
        addr?.let { "${it.street}, ${it.buildingName ?: ""}, ${it.city}, ${it.postalCode}" } ?: "No address selected"
    }

    val orderItemsSummaryText: LiveData<String> = _currentCartItems.map { items ->
        items?.joinToString("\n") { cartItem ->
            "${cartItem.menuItem.name} (x${cartItem.quantity}) - $${String.format("%.2f", cartItem.itemTotalPrice)}"
        } ?: "No items in cart"
    }

    val subtotal: LiveData<Double> = _currentCartItems.map { items ->
        items?.sumOf { it.itemTotalPrice } ?: 0.0
    }

    val subtotalText: LiveData<String> = subtotal.map { subtotalVal ->
        "$${String.format("%.2f", subtotalVal ?: 0.0)}"
    }

    private val _deliveryFee = MutableLiveData(0.0)
    val deliveryFee: LiveData<Double> = _deliveryFee

    val deliveryFeeText: LiveData<String> = _deliveryFee.map { fee ->
        "$${String.format("%.2f", fee ?: 0.0)}"
    }

    val totalAmount: MediatorLiveData<Double> = MediatorLiveData<Double>().apply {
        val update = {
            val currentSubtotal = subtotal.value ?: 0.0
            val currentDeliveryFee = if (currentSubtotal > 0) 3.00 else 0.0
            _deliveryFee.value = currentDeliveryFee
            value = currentSubtotal + currentDeliveryFee
        }
        addSource(subtotal) { update() }
    }

    val totalAmountText: LiveData<String> = totalAmount.map { total ->
        "$${String.format("%.2f", total ?: 0.0)}"
    }

    // --- MODIFIED FOR ORDER CONFIRMATION ---
    private val _navigateToOrderConfirmation = MutableLiveData<Event<Long?>>() // Changed from Boolean to Long?
    val navigateToOrderConfirmation: LiveData<Event<Long?>> = _navigateToOrderConfirmation // Exposed with new type
    // --- END OF MODIFICATION ---

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> = _toastMessage

    init {
        val db = AppDatabase.getInstance(application)
        orderRepository = OrderRepository(db.orderDao(), db.orderItemDao())
        userRepository = UserRepository(db.userDao(), db.addressDao())
        loadDeliveryAddress()
    }

    fun initializeCartItems(items: List<CartDisplayItem>) {
        _currentCartItems.value = items
        val currentSubtotal = items.sumOf { it.itemTotalPrice }
        val currentDeliveryFee = if (currentSubtotal > 0) 3.00 else 0.0
        _deliveryFee.value = currentDeliveryFee
    }

    private fun loadDeliveryAddress() {
        viewModelScope.launch {
            userRepository.getCurrentUserPrimaryAddress().collect { addr ->
                _deliveryAddress.postValue(addr)
                if (addr == null && _currentCartItems.value?.isNotEmpty() == true) {
                    _toastMessage.postValue(Event("Please set up a primary delivery address in your profile."))
                }
            }
        }
    }

    fun onPlaceOrderClicked(specialRequest: String?, cutlery: String?) {
        val userId = _currentUserId.value
        val addr = _deliveryAddress.value
        val itemsToOrder = _currentCartItems.value

        if (userId == null || userId == 0L) {
            _toastMessage.value = Event("User not logged in.")
            return
        }
        if (addr == null) {
            _toastMessage.value = Event("Please select or set a delivery address.")
            return
        }
        if (itemsToOrder.isNullOrEmpty()) {
            _toastMessage.value = Event("Your cart is empty.")
            return
        }

        viewModelScope.launch {
            _toastMessage.postValue(Event("Processing your order..."))
            delay(1500)

            val newOrder = OrderEntity(
                userID = userId,
                addressID = addr.addressID,
                courierID = null,
                payment = "Pay On Delivery (Pseudo)",
                specialRequest = specialRequest,
                paymentStatus = "Pending",
                deliveryStatus = "Pending",
                cutlery = cutlery ?: "Not Specified"
            )

            val orderItemEntities = itemsToOrder.map { cartDisplayItem ->
                OrderItemEntity(
                    orderID = 0L,
                    itemID = cartDisplayItem.menuItem.itemID,
                    quantity = cartDisplayItem.quantity,
                    price = cartDisplayItem.menuItem.price
                )
            }

            Log.d("CheckoutViewModel", "Attempting to create order: $newOrder with items: $orderItemEntities")
            // Assuming orderRepository.createOrderWithItems returns the new orderId (Long) or 0L/negative on failure
            val createdOrderId: Long = orderRepository.createOrderWithItems(newOrder, orderItemEntities)

            // --- MODIFIED FOR ORDER CONFIRMATION ---
            if (createdOrderId > 0L) { // Check if createdOrderId is a valid positive Long
                Log.d("CheckoutViewModel", "Order created with ID: $createdOrderId.")
                _toastMessage.postValue(Event("Order Placed Successfully! Order ID: $createdOrderId"))
                _navigateToOrderConfirmation.postValue(Event(createdOrderId)) // Post the Long orderId
                _currentCartItems.postValue(emptyList())
            } else {
                Log.e("CheckoutViewModel", "Order creation failed in repository.")
                _toastMessage.postValue(Event("Failed to place order. Please try again."))
                _navigateToOrderConfirmation.postValue(Event(null)) // Post null for Long? to indicate failure
            }
            // --- END OF MODIFICATION ---
        }
    }
}