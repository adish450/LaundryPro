package com.laundrypro.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laundrypro.app.data.AdminSessionManager
import com.laundrypro.app.models.*
import com.laundrypro.app.repository.LaundryRepository
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    // This is the fix: Correctly instantiate the repository by passing the ApiService
    private val repository = LaundryRepository()

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _allOrders = MutableLiveData<List<AdminOrder>>()
    val allOrders: LiveData<List<AdminOrder>> = _allOrders

    fun adminLogin(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = LoginResult.Loading
            try {
                // This is the fix: Call the login function with the correct LoginRequest object
                val response = repository.login(email, password)
                AdminSessionManager.saveToken(response.token)
                _loginResult.value = LoginResult.Success(response.user)
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "Admin login failed")
            }
        }
    }

    fun fetchAllOrders() {
        val token = AdminSessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val orders = repository.getAllOrders(token)
                _allOrders.postValue(orders)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}