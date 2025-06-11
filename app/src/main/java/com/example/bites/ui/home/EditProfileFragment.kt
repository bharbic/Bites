package com.example.bites.ui.profile

import android.app.DatePickerDialog
import android.icu.util.Calendar // Use android.icu.util.Calendar for API 24+
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bites.databinding.FragmentEditProfileBinding
import java.text.SimpleDateFormat
import java.util.Locale // Use java.util.Locale with SimpleDateFormat

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var editProfileViewModel: EditProfileViewModel

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editProfileViewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(requireActivity().application, this)
        ).get(EditProfileViewModel::class.java)

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        setupBirthDateInput()
        setupObservers()
        setupClickListeners()

        return binding.root
    }

    private fun setupBirthDateInput() {
        binding.editTextBirthDate.isFocusable = false
        binding.editTextBirthDate.isClickable = true
        binding.editTextBirthDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.layoutEditBirthDate.setOnClickListener { // Also make TextInputLayout clickable
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val existingDateStr = editProfileViewModel.birthDate.value
        if (!existingDateStr.isNullOrEmpty()) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = sdf.parse(existingDateStr)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                Log.e("EditProfileFragment", "Error parsing existing birth date", e)
                calendar.timeInMillis = System.currentTimeMillis()
            }
        } else {
            calendar.timeInMillis = System.currentTimeMillis()
        }

        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDateCalendar = Calendar.getInstance()
                selectedDateCalendar.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val formattedDate = dateFormat.format(selectedDateCalendar.time)
                binding.editTextBirthDate.setText(formattedDate)
                editProfileViewModel.birthDate.value = formattedDate
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setupObservers() {
        // User Profile Fields
        editProfileViewModel.firstName.observe(viewLifecycleOwner) {
            if (binding.editTextFirstName.text.toString() != it) binding.editTextFirstName.setText(it)
        }
        editProfileViewModel.lastName.observe(viewLifecycleOwner) {
            if (binding.editTextLastName.text.toString() != it) binding.editTextLastName.setText(it)
        }
        editProfileViewModel.email.observe(viewLifecycleOwner) {
            if (binding.editTextEmail.text.toString() != it) binding.editTextEmail.setText(it)
        }
        editProfileViewModel.phoneNumber.observe(viewLifecycleOwner) {
            if (binding.editTextPhone.text.toString() != it) binding.editTextPhone.setText(it)
        }
        editProfileViewModel.birthDate.observe(viewLifecycleOwner) {
            if (binding.editTextBirthDate.text.toString() != it) binding.editTextBirthDate.setText(it)
        }

        // Address Fields (Ensure your binding has these IDs from fragment_edit_profile.xml)
        editProfileViewModel.street.observe(viewLifecycleOwner) {
            if (binding.editTextStreet.text.toString() != it) binding.editTextStreet.setText(it)
        }
        editProfileViewModel.city.observe(viewLifecycleOwner) {
            if (binding.editTextCity.text.toString() != it) binding.editTextCity.setText(it)
        }
        editProfileViewModel.country.observe(viewLifecycleOwner) {
            if (binding.editTextCountry.text.toString() != it) binding.editTextCountry.setText(it)
        }
        editProfileViewModel.postalCode.observe(viewLifecycleOwner) {
            if (binding.editTextPostalCode.text.toString() != it) binding.editTextPostalCode.setText(it)
        }
        editProfileViewModel.buildingName.observe(viewLifecycleOwner) {
            val currentText = binding.editTextBuildingName.text.toString()
            if (currentText != (it ?: "")) binding.editTextBuildingName.setText(it ?: "")
        }
        editProfileViewModel.floorNumber.observe(viewLifecycleOwner) {
            val currentText = binding.editTextFloorNumber.text.toString()
            if (currentText != (it ?: "")) binding.editTextFloorNumber.setText(it ?: "")
        }
        editProfileViewModel.doorNumber.observe(viewLifecycleOwner) {
            val currentText = binding.editTextDoorNumber.text.toString()
            if (currentText != (it ?: "")) binding.editTextDoorNumber.setText(it ?: "")
        }
        editProfileViewModel.additionalInfo.observe(viewLifecycleOwner) {
            val currentText = binding.editTextAdditionalInformation.text.toString() // Check your XML ID
            if (currentText != (it ?: "")) binding.editTextAdditionalInformation.setText(it ?: "")
        }


        editProfileViewModel.saveResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { success ->
                if (success) {
                    Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Failed to save. Please check fields.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonSaveProfile.setOnClickListener {
            // Update ViewModel LiveData from EditTexts before calling save
            editProfileViewModel.firstName.value = binding.editTextFirstName.text.toString().trim()
            editProfileViewModel.lastName.value = binding.editTextLastName.text.toString().trim()
            editProfileViewModel.email.value = binding.editTextEmail.text.toString().trim()
            editProfileViewModel.phoneNumber.value = binding.editTextPhone.text.toString().trim()
            // birthDate is updated by the dialog's callback

            // Address Fields
            editProfileViewModel.street.value = binding.editTextStreet.text.toString().trim()
            editProfileViewModel.city.value = binding.editTextCity.text.toString().trim()
            editProfileViewModel.country.value = binding.editTextCountry.text.toString().trim()
            editProfileViewModel.postalCode.value = binding.editTextPostalCode.text.toString().trim()
            editProfileViewModel.buildingName.value = binding.editTextBuildingName.text.toString().trim().takeIf { it.isNotBlank() }
            editProfileViewModel.floorNumber.value = binding.editTextFloorNumber.text.toString().trim().takeIf { it.isNotBlank() }
            editProfileViewModel.doorNumber.value = binding.editTextDoorNumber.text.toString().trim().takeIf { it.isNotBlank() }
            editProfileViewModel.additionalInfo.value = binding.editTextAdditionalInformation.text.toString().trim().takeIf { it.isNotBlank() } // Check your XML ID

            editProfileViewModel.saveProfile()
            Log.d("EditProfileFragment", "Save button clicked. Attempting to save profile.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}