package com.example.bites.ui.home // Or your chosen package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bites.databinding.FragmentOrderHistoryBinding
import com.example.bites.ui.profile.OrderHistoryAdapter
import kotlinx.coroutines.launch

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderHistoryViewModel by viewModels {
        OrderHistoryViewModelFactory(requireActivity().application)
    }
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeOrders()
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter { order ->
            // Handle click on an order item, e.g., navigate to OrderDetailsFragment
            // val action = OrderHistoryFragmentDirections.actionOrderHistoryFragmentToOrderDetailsFragment(order.orderID)
            // findNavController().navigate(action)
            android.widget.Toast.makeText(requireContext(), "Clicked Order ID: ${order.orderID}", android.widget.Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewOrderHistory.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userOrders.collect { orders ->
                    if (orders.isEmpty()) {
                        binding.recyclerViewOrderHistory.visibility = View.GONE
                        binding.textViewNoOrders.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewOrderHistory.visibility = View.VISIBLE
                        binding.textViewNoOrders.visibility = View.GONE
                        orderHistoryAdapter.submitList(orders)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}