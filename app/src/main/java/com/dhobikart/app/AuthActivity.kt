package com.dhobikart.app

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
    private var otpCountDownTimer: CountDownTimer? = null
    private var forgotPasswordStep = 1 // 1 for entering email, 2 for entering OTP/password

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
            forgotPasswordStep = 1
            updateUiForMode()
        }

        binding.textForgotPassword.setOnClickListener {
            isForgotPasswordMode = true
            isLoginMode = false
            forgotPasswordStep = 1
            updateUiForMode()
        }

        binding.textAdminLogin.setOnClickListener {
            startActivity(Intent(this, AdminAuthActivity::class.java))
        }

        binding.btnSendOtp.setOnClickListener {
            handleRequestOtp()
        }

        binding.btnResendOtp.setOnClickListener {
            handleRequestOtp()
        }

        binding.btnResetPassword.setOnClickListener {
            handleResetPassword()
        }

        binding.btnAction.setOnClickListener {
            if (isLoginMode) {
                handleLogin()
            } else { // Register mode
                handleRegister()
            }
        }
    }

    private fun updateUiForMode() {
        otpCountDownTimer?.cancel()
        binding.btnResendOtp.isEnabled = true
        binding.btnResendOtp.text = "Resend OTP"

        // Reset hints to default
        binding.layoutPassword.hint = "Password"

        if (isForgotPasswordMode) {
            if (forgotPasswordStep == 1) {
                // Step 1: Enter email to request OTP
                binding.textTitle.text = "Forgot Password"
                binding.textSubtitle.text = "Enter your email to get an OTP"
                binding.layoutName.visibility = View.GONE
                binding.layoutPhone.visibility = View.GONE
                binding.layoutPassword.visibility = View.GONE
                binding.layoutConfirmPassword.visibility = View.GONE
                binding.layoutOtp.visibility = View.GONE
                binding.btnAction.visibility = View.GONE
                binding.btnSendOtp.visibility = View.VISIBLE
                binding.resetFlowButtonsContainer.visibility = View.GONE
                binding.textSwitchMode.text = "Back to Login"
                binding.textForgotPassword.visibility = View.GONE
            } else { // Step 2: Enter OTP and new password
                binding.textTitle.text = "Reset Password"
                binding.textSubtitle.text = "Enter the OTP and your new password"
                binding.layoutName.visibility = View.GONE
                binding.layoutPhone.visibility = View.GONE
                binding.layoutOtp.visibility = View.VISIBLE
                binding.layoutPassword.visibility = View.VISIBLE
                binding.layoutPassword.hint = "New Password"
                binding.etPassword.text?.clear()
                binding.layoutConfirmPassword.visibility = View.VISIBLE
                binding.etConfirmPassword.text?.clear()
                binding.btnAction.visibility = View.GONE
                binding.btnSendOtp.visibility = View.GONE
                binding.resetFlowButtonsContainer.visibility = View.VISIBLE
                binding.textSwitchMode.text = "Back to Login"
                binding.textForgotPassword.visibility = View.GONE
            }
        } else if (isLoginMode) {
            binding.textTitle.text = "Welcome Back!"
            binding.textSubtitle.text = "Login to continue"
            binding.layoutName.visibility = View.GONE
            binding.layoutPhone.visibility = View.GONE
            binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.GONE
            binding.layoutOtp.visibility = View.GONE
            binding.btnAction.visibility = View.VISIBLE
            binding.btnSendOtp.visibility = View.GONE
            binding.resetFlowButtonsContainer.visibility = View.GONE
            binding.btnAction.text = "Login"
            binding.textSwitchMode.text = "New User? Register here."
            binding.textForgotPassword.visibility = View.VISIBLE
        } else { // Register Mode
            binding.textTitle.text = "Create an Account"
            binding.textSubtitle.text = "Sign up to get started"
            binding.layoutName.visibility = View.VISIBLE
            binding.layoutPhone.visibility = View.VISIBLE
            binding.layoutPassword.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.VISIBLE
            binding.layoutOtp.visibility = View.GONE
            binding.btnAction.visibility = View.VISIBLE
            binding.btnSendOtp.visibility = View.GONE
            binding.resetFlowButtonsContainer.visibility = View.GONE
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

    private fun handleRequestOtp() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.requestOtp(email)
    }

    private fun handleResetPassword() {
        val email = binding.etEmail.text.toString().trim()
        val otp = binding.etOtp.text.toString().trim()
        val newPassword = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.resetPassword(email, otp, newPassword)
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

        viewModel.forgotPasswordOtpSent.observe(this) { otpSent ->
            if (otpSent) {
                Toast.makeText(this, "OTP sent to your email", Toast.LENGTH_SHORT).show()
                forgotPasswordStep = 2
                updateUiForMode()
                startOtpTimer()
            }
        }

        viewModel.passwordResetSuccessful.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show()
                isForgotPasswordMode = false
                isLoginMode = true
                updateUiForMode()
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startOtpTimer() {
        binding.btnResendOtp.isEnabled = false
        otpCountDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.btnResendOtp.text = "Resend OTP in ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                binding.btnResendOtp.isEnabled = true
                binding.btnResendOtp.text = "Resend OTP"
            }
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        val buttonsEnabled = !isLoading
        binding.btnAction.isEnabled = buttonsEnabled
        binding.btnSendOtp.isEnabled = buttonsEnabled
        binding.btnResetPassword.isEnabled = buttonsEnabled
        // Only enable resend if the timer is not active
        binding.btnResendOtp.isEnabled = buttonsEnabled && (otpCountDownTimer == null || !binding.btnResendOtp.text.contains("Resend OTP in"))
    }

    override fun onDestroy() {
        super.onDestroy()
        otpCountDownTimer?.cancel()
    }
}
