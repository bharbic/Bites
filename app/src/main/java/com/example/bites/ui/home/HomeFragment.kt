package com.example.bites.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.bites.databinding.FragmentHomeBinding
import com.example.bites.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val navOptions = NavOptions.Builder()
            // This attempts to mimic the behavior of selecting a new top-level tab:
            // Pop up to the start destination of the graph (Home),
            // effectively clearing the Home tab's specific back stack if any,
            // but save its state. Then navigate to Dashboard.
            .setPopUpTo(R.id.navigation_home, false, true) // Pop to home (exclusive), save state
            .setLaunchSingleTop(true) // Don't add multiple Dashboards if already there
            .build()

        binding.button1.setOnClickListener{
            findNavController().navigate(R.id.navigation_dashboard, null, navOptions)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}