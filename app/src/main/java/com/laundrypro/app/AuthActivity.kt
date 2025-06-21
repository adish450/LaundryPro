
package com.laundrypro.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.laundrypro.app.databinding.ActivityAuthBinding
import com.laundrypro.app.models.LoginResult
import com.laundrypro.app.models.RegisterResult
import com.laundrypro.app.viewmodels.LaundryViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: LaundryViewModel by viewModels()
    private var isLoginMode = true

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
            updateUiForMode()
        }

        binding.btnAction.setOnClickListener {
            if (isLoginMode) {
                handleLogin()
            } else {
                handleRegister()
            }
        }
    }

    private fun updateUiForMode() {
        if (isLoginMode) {
            binding.textTitle.text = "Welcome Back!"
            binding.textSubtitle.text = "Login to continue"
            binding.layoutName.visibility = View.GONE
            binding.layoutPhone.visibility = View.GONE
            binding.layoutConfirmPassword.visibility = View.GONE
            binding.btnAction.text = "Login"
            binding.textSwitchMode.text = "New User? Register here."
        } else {
            binding.textTitle.text = "Create an Account"
            binding.textSubtitle.text = "Sign up to get started"
            binding.layoutName.visibility = View.VISIBLE
            binding.layoutPhone.visibility = View.VISIBLE
            binding.layoutConfirmPassword.visibility = View.VISIBLE
            binding.btnAction.text = "Register"
            binding.textSwitchMode.text = "Already have an account? Login."
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
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnAction.isEnabled = !isLoading
    }
}