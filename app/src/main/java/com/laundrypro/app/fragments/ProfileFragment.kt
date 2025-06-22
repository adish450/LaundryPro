package com.laundrypro.app.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.laundrypro.app.R
import com.laundrypro.app.databinding.FragmentProfileBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.btnManageAddresses.setOnClickListener {
            Toast.makeText(context, "Manage Addresses Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.btnNotificationSettings.setOnClickListener {
            Toast.makeText(context, "Notification Settings Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.btnHelp.setOnClickListener {
            Toast.makeText(context, "Help & Support Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email
            } else {
                // This state should rarely be seen now because of the navigation guard,
                // but it's good practice to handle it.
                binding.tvUserName.text = "Guest User"
                binding.tvUserEmail.text = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}