package com.laundrypro.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laundrypro.app.data.CartManager
import com.laundrypro.app.data.SessionManager
import com.laundrypro.app.models.*
import com.laundrypro.app.repository.LaundryRepository
import kotlinx.coroutines.launch

class LaundryViewModel : ViewModel() {
    private val repository = LaundryRepository()

    val loginResult = MutableLiveData<LoginResult>(LoginResult.Idle)
    val registerResult = MutableLiveData<RegisterResult>(RegisterResult.Idle)
    val navigateToTab = MutableLiveData<Int?>()
    val currentUser = MutableLiveData<User?>()
    val services = MutableLiveData<List<LaundryService>>()
    val offers = MutableLiveData<List<Offer>>()
    val orders = MutableLiveData<List<Order>>()

    // Delegate all cart-related LiveData and functions to the singleton CartManager
    val cartItems = CartManager.cartItems
    val appliedOffer = CartManager.appliedOffer

    // LiveData to signal navigation events
    val navigateToHome = MutableLiveData<Boolean>()

    private val _placeOrderResult = MutableLiveData<PlaceOrderResult>()
    val placeOrderResult: LiveData<PlaceOrderResult> = _placeOrderResult

    init {
        loadServices()
        loadOffers()
        checkUserSession()
    }

    private fun loadServices() {
        viewModelScope.launch {
            services.value = repository.getServices()
        }
    }

    private fun loadOffers() {
        viewModelScope.launch {
            offers.value = repository.getOffers()
        }
    }

    /**
     * Checks SharedPreferences for a saved user session and updates currentUser.
     * This is public so MainActivity can call it in onResume.
     */
    fun checkUserSession() {
        currentUser.value = SessionManager.getUser()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginResult.value = LoginResult.Loading
            try {
                val response = repository.login(email, password)
                SessionManager.saveLoginDetails(response.token, response.user)
                currentUser.value = response.user // Update state
                loginResult.value = LoginResult.Success(response.user)
            } catch (e: Exception) {
                loginResult.value = LoginResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun register(name: String, email: String, password: String, phone: String) {
        viewModelScope.launch {
            registerResult.value = RegisterResult.Loading
            try {
                val response = repository.register(name, email, password, phone)
                // After successful registration, save user session and auto-login
                // You might need to adjust your RegisterResponse to include a token
                // For now, assuming registration doesn't return a token to save.
                // If it does, save it like in the login function.
                SessionManager.saveLoginDetails(response.token, response.user)
                currentUser.value = response.user
                registerResult.value = RegisterResult.Success(response.user)
                loginResult.value = LoginResult.Success(response.user)
            } catch (e: Exception) {
                registerResult.value = RegisterResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun addToCart(item: LaundryItem, serviceId: String) {
        CartManager.addToCart(item, serviceId)
    }

    fun removeFromCart(itemId: String, serviceId: String) {
        CartManager.removeFromCart(itemId, serviceId)
    }

    fun updateCartItemQuantity(itemId: String, serviceId: String, quantity: Int) {
        CartManager.updateCartItemQuantity(itemId, serviceId, quantity)
    }

    fun applyOffer(offerCode: String): Boolean {
        // This logic can stay here if it depends on other ViewModel data
        val offer = offers.value?.find { it.code == offerCode }
        return if (offer != null) {
            CartManager.applyOffer(offer)
            true
        } else {
            false
        }
    }

    fun calculateTotal(): OrderSummary {
        return CartManager.calculateTotal()
    }

    fun placeOrder(pickupAddress: Address) {
        viewModelScope.launch {
            _placeOrderResult.value = PlaceOrderResult.Loading
            try {
                val token = SessionManager.getToken() ?: throw Exception("User not authenticated")
                val user = currentUser.value ?: throw Exception("User not logged in")
                val items = cartItems.value ?: emptyList()
                val total = calculateTotal().total

                val order = repository.placeOrder(token, user, items, total, pickupAddress)

                CartManager.clearCart()
                _placeOrderResult.value = PlaceOrderResult.Success(order)
            } catch (e: Exception) {
                _placeOrderResult.value = PlaceOrderResult.Error(e.message ?: "Unknown error")
            }
        }
    }


    private fun clearCart() {
        cartItems.value = mutableListOf()
        appliedOffer.value = null
    }

    private fun loadUserOrders() {
        currentUser.value?.let { user ->
            viewModelScope.launch {
                orders.value = user.id?.let { repository.getUserOrders(it) }
            }
        }
    }

    fun logout() {
        SessionManager.clear()
        CartManager.clearCart()
        currentUser.value = null
        // Reset states
        loginResult.value = LoginResult.Idle
        registerResult.value = RegisterResult.Idle
        // Trigger the navigation event
        navigateToHome.value = true
    }

    // Call this after the navigation is handled to prevent re-triggering
    fun onHomeNavigationComplete() {
        navigateToHome.value = false
    }
}