package com.example.bites.ui.notifications // Ensure this package is correct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// import android.widget.TextView // Not explicitly used if binding handles title directly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs // For Safe Args
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bites.databinding.FragmentNotificationsBinding // Generated ViewBinding class
import java.text.NumberFormat
import java.util.Locale // For currency formatting

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var notificationsViewModel: NotificationsViewModel
    // Safe Args delegate to get navigation arguments
    private val args: NotificationsFragmentArgs by navArgs()

    private lateinit var cartAdapter: CartDisplayAdapter

    // In NotificationsFragment.kt

// private val args: NotificationsFragmentArgs by navArgs() // This line stays

    // In NotificationsFragment.kt

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // **** INITIALIZE ViewModel FIRST ****
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        // Retrieve cart items from navigation arguments and initialize ViewModel
        val receivedCartItems = args.cartItems // This will now be CartDisplayItem[]? (nullable array)
        val cartItemsList = receivedCartItems?.toList() ?: emptyList() // If null, use an empty list

        // Now it's safe to call this
        notificationsViewModel.initializeCart(cartItemsList)

        observeViewModelData()

        // Optional: If you want to use the LiveData<String> 'text' from ViewModel for the title
        // notificationsViewModel.text.observe(viewLifecycleOwner) { titleText ->
        //     binding.textViewCartScreenTitle.text = titleText
        // }

        binding.buttonConfirmOrder.setOnClickListener {
            // TODO: Implement order confirmation logic
        }

        return root
    }

    private fun setupRecyclerView() {
        cartAdapter = CartDisplayAdapter() // Initialize your adapter
        binding.recyclerViewCartItems.apply {
            adapter = cartAdapter
            // LayoutManager is already set in the XML (app:layoutManager),
            // but you can also set it programmatically if needed:
            // layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModelData() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US) // Or your desired locale

        notificationsViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items)
            // Toggle visibility of empty cart message
            if (items.isNullOrEmpty()) {
                binding.textViewEmptyCartMessage.visibility = View.VISIBLE
                binding.recyclerViewCartItems.visibility = View.GONE
            } else {
                binding.textViewEmptyCartMessage.visibility = View.GONE
                binding.recyclerViewCartItems.visibility = View.VISIBLE
            }
        }

        notificationsViewModel.cartSubtotal.observe(viewLifecycleOwner) { subtotal ->
            binding.textViewCartSubtotalNotifications.text = currencyFormat.format(subtotal)
        }

        // deliveryCost is not LiveData in the ViewModel, so we get it once
        binding.textViewDeliveryFeeNotifications.text = currencyFormat.format(notificationsViewModel.deliveryCost)

        notificationsViewModel.cartTotal.observe(viewLifecycleOwner) { total ->
            binding.textViewCartTotalNotifications.text = currencyFormat.format(total)
            // You could also update the button text if needed, e.g.:
            // binding.buttonConfirmOrder.text = "Confirm Order (${currencyFormat.format(total)})"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to prevent memory leaks
    }
}