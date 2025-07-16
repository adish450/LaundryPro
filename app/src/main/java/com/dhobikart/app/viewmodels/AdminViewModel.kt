package com.dhobikart.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhobikart.app.data.AdminSessionManager
import com.dhobikart.app.models.AdminOrder
import com.dhobikart.app.models.LoginResult
import com.dhobikart.app.repository.LaundryRepository
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository = LaundryRepository()

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _orders = MutableLiveData<List<AdminOrder>>()
    val orders: LiveData<List<AdminOrder>> = _orders

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                // Assuming admin login uses the same response structure
                AdminSessionManager.saveLoginDetails(response.token, response.user)
                _loginResult.value = LoginResult.Success(response.user)
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            try {
                _orders.value = repository.getAllOrders()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}