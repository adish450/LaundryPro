package com.dhobikart.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dhobikart.app.databinding.ActivityAuthBinding
import com.dhobikart.app.models.LoginResult
import com.dhobikart.app.models.RegisterResult
import com.dhobikart.app.viewmodels.LaundryViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: LaundryViewModel by viewModels()
    private var isLoginMode = true
    private var isForgotPasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
        updateUiForMode()
    }

    private fun setupClickListeners() {
        binding.textSwitchMode.setOnClickListener {
            isLoginMode = !isLoginMode
            isForgotPasswordMode = false
            updateUiForMode()
        }

        binding.textForgotPassword.setOnClickListener {
            isForgotPasswordMode = true
            updateUiForMode()
        }

        // Add a click listener for the new admin login text.
        binding.textAdminLogin.setOnClickListener {
            startActivity(Intent(this, AdminAuthActivity::class.java))
        }

        binding.btnAction.setOnClickListener {
            if (isForgotPasswordMode) {
                if (binding.layoutOtp.visibility == View.VISIBLE) {
                    handleResetPassword()
                } else {
                    handleForgotPassword()
                }
            } else if (isLoginMode) {
                handleLogin()
            } else {
                handleRegister()
            }
        }
    }

    // ... rest of the AuthActivity.kt file
    private fun updateUiForMode() {
        if (isForgotPasswordMode) {
            binding.textTitle.text = "Forgot Password"
            binding.textSubtitle.text = "Enter your email to reset your password"
            binding.layoutName.visibility = View.GONE
            binding.layoutPhone.visibility = View.GONE
            binding.layoutPassword.visibility = View.GONE
            binding.layoutConfirmPassword.visibility = View.GONE
            binding.layoutOtp.visibility = View.GONE
            binding.btnAction.text = "Send OTP"
            binding.textSwitchMode.text = "Remembered your password? Login."
            binding.textForgotPassword.visibility = View.GONE
        } else if (isLoginMode) {
            binding.textTitle.text = "Welcome Back!"
            binding.textSubtitle.text = "Login to continue"
            binding.layoutName.visibility = View.GONE
            binding.layoutPhone.visibility = View.GONE
            binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.GONE
            binding.layoutOtp.visibility = View.GONE
            binding.btnAction.text = "Login"
            binding.textSwitchMode.text = "New User? Register here."
            binding.textForgotPassword.visibility = View.GONE
        } else {
            binding.textTitle.text = "Create an Account"
            binding.textSubtitle.text = "Sign up to get started"
            binding.layoutName.visibility = View.VISIBLE
            binding.layoutPhone.visibility = View.VISIBLE
            binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.VISIBLE
            binding.layoutOtp.visibility = View.GONE
            binding.btnAction.text = "Register"
            binding.textSwitchMode.text = "Already have an account? Login."
            binding.textForgotPassword.visibility = View.GONE
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.login(email, password)
    }

    private fun handleRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.register(name, email, password, phone)
    }

    private fun handleForgotPassword() {
        val email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        // TODO: forgot password
        //viewModel.forgotPassword(email)
    }

    private fun handleResetPassword() {
        val email = binding.etEmail.text.toString().trim()
        val otp = binding.etOtp.text.toString().trim()
        val newPassword = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: forgot password
        //viewModel.resetPassword(email, otp, newPassword)
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Loading -> showLoading(true)
                is LoginResult.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is LoginResult.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Login Failed: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> showLoading(false)
            }
        }

        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Loading -> showLoading(true)
                is RegisterResult.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is RegisterResult.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Registration Failed: ${result.message}", Toast.LENGTH_LONG).show()
                }
                else -> showLoading(false)
            }
        }

        // TODO: forgot password
        /*viewModel.forgotPasswordOtpSent.observe(this) { otpSent ->
            if (otpSent) {
                binding.layoutOtp.visibility = View.VISIBLE
                binding.layoutPassword.visibility = View.VISIBLE
                binding.etPassword.hint = "New Password"
                binding.btnAction.text = "Reset Password"
                Toast.makeText(this, "OTP sent to your email", Toast.LENGTH_SHORT).show()
            }
        }*/

        /*viewModel.passwordResetSuccessful.observe(this) { resetSuccessful ->
            if (resetSuccessful) {
                isForgotPasswordMode = false
                isLoginMode = true
                updateUiForMode()
                Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show()
            }
        }*/

        /*viewModel.error.observe(this) { error ->
            if (error != null) {
                showLoading(false)
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }*/
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAction.isEnabled = !isLoading
    }
}