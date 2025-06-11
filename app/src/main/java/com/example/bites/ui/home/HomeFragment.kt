package com.example.bites.ui.home

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bites.data.entity.AddressEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.bites.R
import com.example.bites.data.entity.UserEntity
import com.example.bites.databinding.FragmentHomeBinding
import kotlin.text.filter
import kotlin.text.isNotBlank

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this,
            HomeViewModelFactory(requireActivity().application)
        ).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupObservers()
        setupClickListeners()

        return binding.root
    }

    private fun setupObservers() {
        // Observe the 'text' LiveData
        homeViewModel.text.observe(viewLifecycleOwner, Observer { newText ->
            // Example: Update a TextView in your layout if you have one for this
            // binding.someTextViewForHomeTitle.text = newText
            Log.d("HomeFragment", "Observed text: $newText")
        })

        // Observe currentUser LiveData
        homeViewModel.currentUser.observe(viewLifecycleOwner, Observer { userEntity ->
            updateProfileUI(userEntity, homeViewModel.currentUserAddress.value)
            Log.d("HomeFragment", "Current user observed: $userEntity")
        })

        // Observe currentUserAddress LiveData
        homeViewModel.currentUserAddress.observe(viewLifecycleOwner, Observer { addressEntity ->
            updateProfileUI(homeViewModel.currentUser.value, addressEntity)
            Log.d("HomeFragment", "Current user address observed: $addressEntity")
        })

        // Observe navigateToEditProfile LiveData
        homeViewModel.navigateToEditProfile.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { // Only proceed if the event has not been handled
                Log.d("HomeFragment", "Navigating to EditProfile")
                // Make sure you have the correct navigation action ID defined in your nav_graph.xml
                // Example: <action android:id="@+id/action_HomeFragment_to_EditProfileFragment" ... />
                try {
                    findNavController().navigate(R.id.action_navigation_home_to_editProfileFragment)
                } catch (e: IllegalArgumentException) {
                    Log.e("HomeFragment", "Navigation action to EditProfileFragment not found. Check your nav_graph.xml", e)
                    // You might want to show a Toast or handle this error more gracefully
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.buttonEditProfile.setOnClickListener {
            homeViewModel.onEditProfileClicked()
        }

        binding.buttonMyOrders.setOnClickListener { // Assuming you add such a button
        findNavController().navigate(R.id.action_homeFragment_to_orderHistoryFragment)
          }

        binding.buttonStartOrder.setOnClickListener {
            // Using the global action you defined earlier if it's still relevant
            // Otherwise, navigate directly to dashboard if that's the intent
            try {
                findNavController().navigate(R.id.action_homeToDash)
            } catch (e: Exception) {
                // Fallback or direct navigation if action_homeToDash is not found/appropriate
                Log.e("HomeFragment", "Failed to navigate with action_homeToDash, trying direct nav.", e)
                findNavController().navigate(R.id.navigation_dashboard)
            }
        }
    }



    private fun updateProfileUI(user: UserEntity?, address: AddressEntity?) { // Now takes address
        if (user != null) {
            binding.textViewUserFullName.text = "${user.firstName} ${user.lastName}"
            binding.textViewUserEmail.text = user.mail
            binding.textViewUserPhoneNumber.text = "Phone: ${user.phoneNumber}"
            binding.imageViewUserProfile.setImageResource(R.mipmap.ic_launcher)
        } else {
            binding.textViewUserFullName.text = "Welcome Guest"
            binding.textViewUserEmail.text = "Please set up your profile"
            binding.textViewUserPhoneNumber.visibility = View.GONE
            binding.imageViewUserProfile.setImageResource(R.mipmap.ic_launcher)
        }

        if (address != null) {
            // Construct address string (customize as needed)
            val addressString = listOfNotNull(
                address.street,
                address.buildingName,
                address.city,
                address.country,
                address.postalCode
            ).filter { it.isNotBlank() }.joinToString(", ")

            if (addressString.isNotBlank()) {
                binding.textViewUserAddress.text = addressString
                binding.textViewUserAddress.visibility = View.VISIBLE
            } else {
                binding.textViewUserAddress.visibility = View.GONE
            }
        } else {
            binding.textViewUserAddress.text = "No address on file" // Or just GONE
            binding.textViewUserAddress.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ViewModelFactory for HomeViewModel (if not already defined elsewhere globally)
class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}