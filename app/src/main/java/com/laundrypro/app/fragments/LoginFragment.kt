package com.laundrypro.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.laundrypro.app.R
import com.laundrypro.app.databinding.FragmentLoginBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // This gets the ViewModel instance that was created in AuthActivity
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // This call will now resolve correctly
                viewModel.login(email, password) { success, message ->
                    if (success) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        activity?.finish() // Close AuthActivity and return
                    } else {
                        Toast.makeText(context, "Login Failed: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            // Logic to switch to the register tab can be handled via the view model or an interface
            // For now, this is a placeholder
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}