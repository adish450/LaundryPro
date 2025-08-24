package com.dhobikart.app.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.AuthActivity
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentSignupBinding
import com.dhobikart.app.models.RegisterResult
import com.dhobikart.app.viewmodels.LaundryViewModel

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupBinding.bind(view)

        val spannable = SpannableString("I agree to the Terms & Conditions.")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Open Terms & Conditions page
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourdomain.com/terms"))
                startActivity(intent)
            }
        }

        spannable.setSpan(clickableSpan, 15, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTerms.text = spannable
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()

        // disable by default
        binding.btnSignup.isEnabled = false
        binding.btnSignup.alpha = 0.5f // dim look when disabled

        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSignup.isEnabled = isChecked
            binding.btnSignup.alpha = if (isChecked) 1f else 0.5f
        }

        binding.btnGoToLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (password != confirmPassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(name, email, password, phone)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegisterResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignup.isEnabled = false
                }
                is RegisterResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.isEnabled = true
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                    (activity as? AuthActivity)?.onAuthSuccess()
                }
                is RegisterResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignup.isEnabled = true
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
