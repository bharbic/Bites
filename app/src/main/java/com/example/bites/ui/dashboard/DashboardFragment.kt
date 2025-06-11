package com.example.bites.ui.dashboard // Make sure this package name is correct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // Correct import
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bites.databinding.FragmentDashboardBinding // Make sure this is correct
import androidx.navigation.fragment.findNavController

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var shopAdapter: ShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Initialize ViewModel - This should now work correctly
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        // Initialize Adapter with the click listener
        shopAdapter = ShopAdapter { shop ->
            // Handle shop item click
            Toast.makeText(context, "Clicked: ${shop.name} (ID: ${shop.shopID})", Toast.LENGTH_SHORT).show()

            // --- NAVIGATION CODE STARTS HERE ---
            // Create the action, providing the arguments defined in the nav graph
            // Make sure shop.shopID matches the argType you defined (string, long, etc.)
            val action = DashboardFragmentDirections.actionDashboardFragmentToShopMenuFragment(
                shopId = shop.shopID.toString(), // Pass the shop's ID
                shopName = shop.name    // Pass the shop's name
            )

            // Perform the navigation
            findNavController().navigate(action)
            // --- NAVIGATION CODE ENDS HERE ---
        }

        // Setup RecyclerView
        binding.recyclerViewShops.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shopAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe LiveData from ViewModel
        dashboardViewModel.allShops.observe(viewLifecycleOwner) { shops ->
            shops?.let {
                shopAdapter.submitList(it) // Use submitList with ListAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewShops.adapter = null // Clear adapter reference
        _binding = null
    }
}