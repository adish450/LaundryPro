package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentProfileBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        observeViewModel()
        setupClickListeners()
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.profileName.text = user.name
                // In a real app, you would load the profile image with Glide or Picasso
                // binding.profileImage.load(user.profileImageUrl)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnPersonalInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnContactInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnAddress.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageAddressesFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnNotifications.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationSettingsFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnPaymentMethods.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PaymentMethodsFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnOrderHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
