package com.laundrypro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.laundrypro.app.databinding.ActivityAdminAuthBinding
import com.laundrypro.app.models.LoginResult
import com.laundrypro.app.viewmodels.AdminViewModel

class AdminAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAuthBinding
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdminLogin.setOnClickListener {
            val email = binding.etAdminEmail.text.toString().trim()
            val password = binding.etAdminPassword.text.toString().trim()
            viewModel.adminLogin(email, password)
        }

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is LoginResult.Loading -> {
                    // Show loading indicator
                }
                else -> {}
            }
        }
    }
}
