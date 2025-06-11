package com.example.bites.ui.dashboard // Or your actual package

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bites.databinding.FragmentShopMenuBinding
import com.example.bites.ui.adapters.MenuAdapter
// import com.example.bites.ui.adapters.YourMenuAdapter // <-- IMPORT YOUR ACTUAL MENU ADAPTER
// import com.example.bites.data.entity.MenuItemEntity // Might be needed for adapter click lambda
import java.text.NumberFormat // For currency formatting
import java.util.Locale
import kotlin.collections.firstOrNull
import kotlin.collections.isNullOrEmpty
import kotlin.collections.toTypedArray

class ShopMenuFragment : Fragment() {

    private var _binding: FragmentShopMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var menuAdapter: MenuAdapter

    private lateinit var shopMenuViewModel: ShopMenuViewModel
    private val args: ShopMenuFragmentArgs by navArgs() // Gets shopId as String, shopName as String?

    // TODO: Replace YourMenuAdapter with your actual adapter class name
    // private lateinit var menuAdapter: YourMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopMenuBinding.inflate(inflater, container, false)

        Log.d("ShopMenuFragment", "onCreateView: Received shopId (String from NavArgs): ${args.shopId}, shopName: ${args.shopName}")

        val shopIdString = args.shopId
        val shopIdAsInt = shopIdString.toIntOrNull()

        if (shopIdAsInt == null) {
            Log.e("ShopMenuFragment", "Critical error: shopId from args ('$shopIdString') could not be converted to Int.")
            Toast.makeText(context, "Error: Could not load shop menu. Invalid shop ID.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack() // Go back if ID is invalid
            return binding.root // Stop further processing to avoid crashes
        }

        // Initialize ViewModel using the Factory and the converted shopIdAsInt
        val viewModelFactory = ShopMenuViewModelFactory(
            requireActivity().application,
            shopIdAsInt // Pass the successfully converted Int
        )
        shopMenuViewModel = ViewModelProvider(this, viewModelFactory).get(ShopMenuViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter { menuItem -> // Your onItemClicked lambda
            shopMenuViewModel.addItemToCart(menuItem)
            Toast.makeText(context, "${menuItem.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewMenuItems.apply { // Make sure this ID exists in your XML
            adapter = menuAdapter
            layoutManager = LinearLayoutManager(context)
        }
        Log.d("ShopMenuFragment", "RecyclerView setup COMPLETE.") // Add a log
    }







    private fun observeViewModel() {
        // Observe Menu Items (This part should be working)
        shopMenuViewModel.menuItemsForShop.observe(viewLifecycleOwner) { menuItems ->
            Log.d("ShopMenuFragment", "Observed menu items. Count: ${menuItems?.size}")
            menuAdapter.submitList(menuItems)
            // ... (empty menu message logic if you have it) ...
        }

        // Observe Cart Subtotal
        shopMenuViewModel.cartSubtotal.observe(viewLifecycleOwner) { subtotal ->
            Log.d("ShopMenuFragment", "Observed subtotal: $subtotal")
            // *** UNCOMMENT AND ENSURE ID IS CORRECT ***
            binding.textViewCartSubtotal.text = formatCurrency(subtotal)
        }

        // Set Delivery Fee (not LiveData, set once or observe if it becomes LiveData)
        val deliveryFee = shopMenuViewModel.deliveryCost // Assuming deliveryCost is a val in VM
        Log.d("ShopMenuFragment", "Setting delivery fee: $deliveryFee")
        // *** UNCOMMENT AND ENSURE ID IS CORRECT ***
        binding.textViewDeliveryFee.text = formatCurrency(deliveryFee)

        // Observe Cart Total
        shopMenuViewModel.cartTotal.observe(viewLifecycleOwner) { total ->
            Log.d("ShopMenuFragment", "Observed total: $total")
            // *** UNCOMMENT AND ENSURE IDS ARE CORRECT ***
            binding.textViewCartTotal.text = formatCurrency(total)
            binding.buttonCheckout.text = "Checkout (${formatCurrency(total)})"
        }
    }

    // Also, in onViewCreated for the shop name:
    // In ShopMenuFragment.kt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.shopName?.let { name ->
            requireActivity().title = "Order from $name"
            Log.d("ShopMenuFragment", "Setting shop name: $name")
        }

        // THIS IS WHERE THE LISTENER IS SET UP
        binding.buttonCheckout.setOnClickListener {
            Log.d("ShopMenuFragment", "--- CHECKOUT BUTTON CLICKED ---") // This log should ALWAYS appear when you click
            val currentCartItemsForNav = shopMenuViewModel.getCurrentCartForNavigation()
            Log.d("ShopMenuFragment", "Checkout clicked. Cart items for nav: ${currentCartItemsForNav?.size ?: "null or empty"}")

            if (!currentCartItemsForNav.isNullOrEmpty()) {
                // MOVED ALL THE NAVIGATION LOGIC INSIDE THE CLICK LISTENER
                Log.d("ShopMenuFragment", "Inside if-block. currentCartItemsForNav size: ${currentCartItemsForNav.size}")

                val cartItemsArray: Array<com.example.bites.data.entity.CartDisplayItem> // Use your actual CartDisplayItem
                try {
                    // Ensure currentCartItemsForNav is List<CartDisplayItem>
                    cartItemsArray = currentCartItemsForNav.toTypedArray()
                    Log.d("ShopMenuFragment", "toTypedArray() successful. Array size: ${cartItemsArray.size}")
                } catch (e: Exception) {
                    Log.e("ShopMenuFragment", "Error in toTypedArray()", e)
                    return@setOnClickListener // Stop further execution in listener
                }

                val firstItem = cartItemsArray.firstOrNull()
                if (firstItem != null) {
                    // Accessing menuItem.name from CartDisplayItem
                    Log.d("ShopMenuFragment", "First item name: ${firstItem.menuItem.name}")
                } else {
                    Log.d("ShopMenuFragment", "CartItemsArray is empty or firstItem is null after conversion.")
                }

                try {
                    Log.d("ShopMenuFragment", "Attempting to create navigation action...")
                    // Ensure ShopMenuFragmentDirections is generated and this action exists
                    val action = ShopMenuFragmentDirections.actionShopMenuFragmentToNotificationsFragment(cartItemsArray)
                    Log.d("ShopMenuFragment", "Navigation action created successfully.")

                    findNavController().navigate(action)
                    Log.d("ShopMenuFragment", "navigate(action) called.")
                } catch (e: Exception) {
                    Log.e("ShopMenuFragment", "Error creating or executing navigation action", e)
                    Toast.makeText(context, "Error proceeding to checkout. Please try again.", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.w("ShopMenuFragment", "Checkout attempted with empty cart.")
                Toast.makeText(context, "Your cart is empty. Add items to proceed.", Toast.LENGTH_LONG).show()
            }
        } // <<<<<<<<<<<< THE CLICK LISTENER BLOCK ENDS HERE

        // THE CODE THAT WAS HERE (getting currentCartItemsForNav, creating array, navigating)
        // HAS BEEN MOVED *INSIDE* THE setOnClickListener ABOVE.
        // DELETE IT FROM HERE.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // binding.recyclerViewMenuItems.adapter = null // Clear adapter if RecyclerView exists
        _binding = null
    }

    // Helper function for formatting currency
    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
    }
}