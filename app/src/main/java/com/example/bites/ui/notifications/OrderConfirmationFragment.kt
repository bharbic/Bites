package com.example.bites.ui.notifications // Adjust package if needed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bites.databinding.FragmentOrderConfirmationBinding // Generated binding class

class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!

    // Safe Args delegate
    private val args: OrderConfirmationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = args.orderId
        binding.textViewOrderIdValue.text = orderId.toString()

        binding.buttonBackToHome.setOnClickListener {
            navigateToHome()
        }

        // Handle back press to ensure it goes to home as defined by navigation action
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        if (isAdded) { // Ensure fragment is still added to an activity
            val action = OrderConfirmationFragmentDirections.actionOrderConfirmationFragmentToNavigationHome()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}