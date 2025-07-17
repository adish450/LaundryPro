package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentEditProfileBinding
import com.dhobikart.app.models.UpdateProfileResult
import com.dhobikart.app.viewmodels.LaundryViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        populateUserDetails()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun populateUserDetails() {
        viewModel.currentUser.value?.let { user ->
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone)
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveChanges.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                viewModel.updateUserProfile(name, email, phone)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.updateProfileResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UpdateProfileResult.Loading -> {
                    binding.btnSaveChanges.isEnabled = false
                    binding.btnSaveChanges.text = "Saving..."
                }
                is UpdateProfileResult.Success -> {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                is UpdateProfileResult.Error -> {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"
                    Toast.makeText(context, "Failed to update profile: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is UpdateProfileResult.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Reset the result state when the view is destroyed
        viewModel.onUpdateProfileHandled()
        _binding = null
    }
}