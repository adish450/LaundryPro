package com.dhobikart.app.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dhobikart.app.databinding.FragmentNotificationSettingsBinding

class NotificationSettingsFragment : Fragment() {

    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    companion object {
        const val PREFS_NAME = "NotificationPrefs"
        const val KEY_PUSH = "push_notifications"
        const val KEY_OFFERS = "special_offers"
        const val KEY_ORDERS = "order_updates"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setupToolbar()
        loadSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadSettings() {
        binding.switchPushNotifications.isChecked = prefs.getBoolean(KEY_PUSH, true)
        binding.switchSpecialOffers.isChecked = prefs.getBoolean(KEY_OFFERS, true)
        binding.switchOrderUpdates.isChecked = prefs.getBoolean(KEY_ORDERS, true)
    }

    private fun setupListeners() {
        binding.switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            saveSetting(KEY_PUSH, isChecked)
        }
        binding.switchSpecialOffers.setOnCheckedChangeListener { _, isChecked ->
            saveSetting(KEY_OFFERS, isChecked)
        }
        binding.switchOrderUpdates.setOnCheckedChangeListener { _, isChecked ->
            saveSetting(KEY_ORDERS, isChecked)
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}