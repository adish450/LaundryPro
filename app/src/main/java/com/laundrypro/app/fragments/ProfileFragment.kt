package com.laundrypro.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.laundrypro.app.AuthActivity
import com.laundrypro.app.R
import com.laundrypro.app.databinding.FragmentProfileBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupClickListeners()
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
            // After logout, you might want to switch to the Home fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email
                binding.tvUserPhone.text = user.phone

                // Find default address and display it
                val defaultAddress = user.addresses?.find { it.isDefault }?.fullAddress
                binding.textUserAddress.text = defaultAddress ?: "No default address set."

                binding.btnLogout.visibility = View.VISIBLE
            } else {
                // This case should ideally not be hit if the fragment is protected,
                // but as a fallback, redirect to AuthActivity.
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}