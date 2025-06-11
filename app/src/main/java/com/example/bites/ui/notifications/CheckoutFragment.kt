package com.example.bites.ui.notifications // Or com.example.bites.ui.checkout if you moved it

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.ui.semantics.text
// import androidx.compose.ui.semantics.text // Unused import, can be removed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer // Keep this specific Observer import
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.bites.ui.notifications.CheckoutFragment
// import androidx.lifecycle.observe // This specific import might be from an older library or not needed if using androidx.lifecycle.Observer explicitly
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bites.R
import com.example.bites.databinding.FragmentCheckoutBinding
// Unused kotlin.text imports, can be removed
// import kotlin.text.get
// import kotlin.text.isNotEmpty
// import kotlin.text.toList
// import kotlin.text.trim


// Make sure your NavArgs class is correctly imported.
// If your nav graph is in the root 'navigation' folder and your fragment is here,
// the generated class might be in a different package relative to this fragment.
// Example: import com.example.bites.CheckoutFragmentArgs - check your build/generated/source/navigation-args folder
// Or if you moved this fragment to ui.checkout, the relative path might be simpler.
// For now, assuming it resolves correctly or you have the right import.

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkoutViewModel: CheckoutViewModel
    // This assumes CheckoutFragmentArgs is generated and accessible.
    // If CheckoutFragment is in 'com.example.bites.ui.notifications'
    // and your nav graph is, for example, 'com.example.bites.navigation.mobile_navigation'
    // the generated NavArgs for a destination named 'checkoutFragment' in that graph
    // would typically be 'com.example.bites.ui.notifications.CheckoutFragmentArgs' if the fragment's name attribute matches.
    // However, if the nav graph is defined at a higher level (e.g. just `com.example.bites`), then Args might be in `com.example.bites.CheckoutFragmentArgs`.
    // Let's assume the IDE or your setup resolves this correctly.
    private val checkoutArgs: CheckoutFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        // It's good practice to initialize ViewModel in onViewCreated or onCreate
        // but if done here, ensure it's not re-created unnecessarily on config changes
        // if this fragment is retained. However, for non-retained fragments, this is okay.
        // Moving to onViewCreated is generally safer.
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize ViewModel
        // If CheckoutViewModel has dependencies, you'll need a ViewModelProvider.Factory
        checkoutViewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        // Initialize ViewModel with cart items from Safe Args
        // Ensure checkoutArgs.checkoutCartItems is the correct name from your nav_graph argument
        val receivedCartItems = checkoutArgs.checkoutCartItems?.toList() ?: emptyList()
        checkoutViewModel.initializeCartItems(receivedCartItems)
        Log.d("CheckoutFragment", "Received ${receivedCartItems.size} items for checkout.")

        setupSpinners()
        setupObservers()
        setupClickListeners()
    }

    private fun setupSpinners() {
        // Setup for Cutlery Spinner
        // Ensure R.array.cutlery_options_array exists in your strings.xml
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cutlery_options_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCutlery.adapter = adapter
        }
        // binding.spinnerCutlery.setSelection(0) // Optionally set a default
    }

    private fun setupClickListeners() {
        binding.buttonPlaceOrder.setOnClickListener {
            val specialRequest = binding.editTextSpecialRequests.text.toString().trim()
            val selectedCutlery = binding.spinnerCutlery.selectedItem.toString()

            // Call ViewModel to handle order placement
            checkoutViewModel.onPlaceOrderClicked(
                specialRequest = if (specialRequest.isNotEmpty()) specialRequest else null,
                cutlery = selectedCutlery // Make sure your ViewModel expects a String for cutlery
            )
        }
    }

    private fun setupObservers() {
        checkoutViewModel.deliveryAddressText.observe(viewLifecycleOwner, Observer { addressText ->
            binding.textViewDeliveryAddress.text = addressText
        })
        checkoutViewModel.orderItemsSummaryText.observe(viewLifecycleOwner, Observer { summaryText ->
            binding.textViewOrderItems.text = summaryText
        })
        checkoutViewModel.subtotalText.observe(viewLifecycleOwner, Observer { subtotal ->
            binding.textViewSubtotalValue.text = subtotal
        })
        checkoutViewModel.deliveryFeeText.observe(viewLifecycleOwner, Observer { fee ->
            binding.textViewDeliveryFeeValue.text = fee
        })
        checkoutViewModel.totalAmountText.observe(viewLifecycleOwner, Observer { total ->
            binding.textViewTotalAmountValue.text = total
        })

        checkoutViewModel.toastMessage.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        })

        // *** THIS IS THE KEY CHANGE ***
        // Observe the LiveData that carries the orderId for navigation
        checkoutViewModel.navigateToOrderConfirmation.observe(viewLifecycleOwner, Observer { event -> // <<<< CHANGED HERE
            event.getContentIfNotHandled()?.let { actualOrderId -> // actualOrderId will now correctly be Long?
                if (actualOrderId != null && actualOrderId > 0L) { // Success: actualOrderId is a valid Long
                    Log.d(
                        "CheckoutFragment",
                        "Order successful, navigating to confirmation. Order ID: $actualOrderId"
                    )
                    val action = CheckoutFragmentDirections
                        .actionCheckoutFragmentToOrderConfirmationFragment(actualOrderId) // Passing Long
                    findNavController().navigate(action)
                } else { // Order failed (actualOrderId is null or not a positive Long)
                    Log.d("CheckoutFragment", "Order failed. Toast is shown by ViewModel.")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important for preventing memory leaks with View Binding
    }
}

