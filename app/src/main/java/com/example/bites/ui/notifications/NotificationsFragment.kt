package com.example.bites.ui.notifications // Ensure this package is correct

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.semantics.text
// import androidx.compose.ui.semantics.text // Not used, can be removed
// import android.widget.TextView // Not explicitly used if binding handles title directly
import androidx.fragment.app.Fragment
// import androidx.glance.visibility // Not used for View system, can be removed
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs // For Safe Args
import com.example.bites.R
// import androidx.recyclerview.widget.LinearLayoutManager // Only needed if setting programmatically and not in XML
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel FIRST
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        // Retrieve cart items from navigation arguments and initialize ViewModel
        val receivedCartItems = args.cartItems
        val cartItemsList = receivedCartItems?.toList() ?: emptyList()

        notificationsViewModel.initializeCart(cartItemsList)

        observeViewModelData()

        binding.buttonConfirmOrder.setOnClickListener {
            val currentCartItemsArray = notificationsViewModel.cartItems.value?.toTypedArray()

            if (currentCartItemsArray.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show()
                Log.d("NotificationsFragment", "Attempted to confirm order with an empty cart.")
                return@setOnClickListener
            }

            // Use the generated NavDirections class
            val action = NotificationsFragmentDirections
                .actionNavigationNotificationsToCheckoutFragment(currentCartItemsArray)
            findNavController().navigate(action)
            Log.d("NotificationsFragment", "Confirm Order button clicked, navigating to Checkout with ${currentCartItemsArray.size} items.")
        }

        return root
    }

    private fun setupRecyclerView() {
        cartAdapter = CartDisplayAdapter { cartItemToRemove ->
            notificationsViewModel.removeItemFromCart(cartItemToRemove)
        }
        binding.recyclerViewCartItems.apply {
            adapter = cartAdapter
            // If LayoutManager is not set in XML, you would set it here:
            // layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModelData() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US) // Or your desired locale

        // Observer 1: cartItems
        notificationsViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            Log.d(
                "NotificationsFragment",
                "cartItems LiveData OBSERVED. Item count: ${items?.size ?: "null"}. Adapter is null: ${cartAdapter == null}"
            )

            if (binding.recyclerViewCartItems.adapter == null) {
                Log.e(
                    "NotificationsFragment",
                    "RecyclerView ADAPTER IS NULL just before submitList! Setting it again."
                )
                binding.recyclerViewCartItems.adapter = cartAdapter
            }

            // Prefer submitting the non-null list directly if possible, or an empty list if items is null.
            // Since your ViewModel's _cartItems is LiveData<List<CartDisplayItem>>(emptyList()),
            // 'items' here should ideally not be null.
            cartAdapter.submitList(items ?: emptyList())
            Log.d(
                "NotificationsFragment",
                "Submitting list to adapter. Item count: ${items?.size ?: "0"}. First item if any: ${items?.firstOrNull()?.menuItem?.name}"
            )


            if (items.isNullOrEmpty()) {
                binding.textViewEmptyCartMessage.visibility = View.VISIBLE
                binding.recyclerViewCartItems.visibility = View.GONE
                Log.d("NotificationsFragment", "Cart is empty, showing empty message.")
            } else {
                binding.textViewEmptyCartMessage.visibility = View.GONE
                binding.recyclerViewCartItems.visibility = View.VISIBLE
                Log.d("NotificationsFragment", "Cart has items, showing RecyclerView.")
            }
        }

        // Observer 2: cartSubtotal
        notificationsViewModel.cartSubtotal.observe(viewLifecycleOwner) { subtotal ->
            binding.textViewCartSubtotalNotifications.text = currencyFormat.format(subtotal)
            Log.d("NotificationsFragment", "cartSubtotal LiveData OBSERVED: $subtotal")
        }

        // Setting delivery fee (not an observer, as it's a fixed value in ViewModel)
        binding.textViewDeliveryFeeNotifications.text = currencyFormat.format(notificationsViewModel.deliveryCost)

        // Observer 3: cartTotal
        notificationsViewModel.cartTotal.observe(viewLifecycleOwner) { total ->
            binding.textViewCartTotalNotifications.text = currencyFormat.format(total)
            Log.d("NotificationsFragment", "cartTotal LiveData OBSERVED: $total")
            // You could also update the button text if needed, e.g.:
            // binding.buttonConfirmOrder.text = "Confirm Order (${currencyFormat.format(total)})"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Important to prevent memory leaks with ViewBinding in Fragments
        binding.recyclerViewCartItems.adapter = null // Recommended for RecyclerView
        _binding = null
    }
}