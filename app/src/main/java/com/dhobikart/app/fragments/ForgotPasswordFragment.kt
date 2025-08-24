package com.dhobikart.app.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentForgotPasswordBinding
import com.dhobikart.app.models.ForgotPasswordResult
import com.dhobikart.app.viewmodels.LaundryViewModel

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private var timer: CountDownTimer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPasswordBinding.bind(view)

        binding.btnAction.setOnClickListener {
            if (binding.layoutOtp.visibility == View.GONE) {
                val email = binding.etEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    viewModel.requestOtp(email)
                } else {
                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                }
            } else {
                val email = binding.etEmail.text.toString().trim()
                val otp = binding.etOtp.text.toString().trim()
                val newPassword = binding.etNewPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()

                if (newPassword != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewModel.resetPassword(email, otp, newPassword)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.forgotPasswordResult.observe(viewLifecycleOwner) { result ->
            when(result) {
                is ForgotPasswordResult.OtpSent -> {
                    binding.layoutOtp.visibility = View.VISIBLE
                    binding.layoutNewPassword.visibility = View.VISIBLE
                    binding.layoutConfirmPassword.visibility = View.VISIBLE
                    binding.btnAction.text = "Reset Password"
                    binding.etEmail.isEnabled = false
                    startTimer()
                }
                is ForgotPasswordResult.Success -> {
                    Toast.makeText(context, "Password reset successfully", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
                is ForgotPasswordResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun startTimer() {
        binding.textTimer.visibility = View.VISIBLE
        timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textTimer.text = "Resend OTP in ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.textTimer.visibility = View.GONE
                binding.etEmail.isEnabled = true
                binding.btnAction.text = "Send OTP"
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}