package com.laundrypro.app.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val offers = MutableLiveData<List<Offer>>()
    val orders = MutableLiveData<List<Order>>()
    val appliedOffer = MutableLiveData<Offer?>()

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

    fun onNavigateTo(tabIndex: Int) {
        navigateToTab.value = tabIndex
    }

    fun onNavigationComplete() {
        navigateToTab.value = null
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

    fun addToCart(item: LaundryItem, serviceId: Int) {
        val currentCart = cartItems.value ?: mutableListOf()
        val existingItem = currentCart.find { it.itemId == item.id && it.serviceId == serviceId }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(item.id, item.name, item.price, 1, serviceId))
        }
        cartItems.value = currentCart
    }

    fun removeFromCart(itemId: String, serviceId: Int) {
        val currentCart = cartItems.value ?: mutableListOf()
        currentCart.removeAll { it.itemId == itemId && it.serviceId == serviceId }
        cartItems.value = currentCart
    }

    fun updateCartItemQuantity(itemId: String, serviceId: Int, quantity: Int) {
        val currentCart = cartItems.value ?: mutableListOf()
        if (quantity <= 0) {
            removeFromCart(itemId, serviceId)
        } else {
            currentCart.find { it.itemId == itemId && it.serviceId == serviceId }?.quantity = quantity
            cartItems.value = currentCart
        }
    }

    fun applyOffer(offerCode: String): Boolean {
        val offer = offers.value?.find { it.code == offerCode }
        return if (offer != null) {
            appliedOffer.value = offer
            true
        } else {
            false
        }
    }

    fun calculateTotal(): OrderSummary {
        val items = cartItems.value ?: mutableListOf()
        val subtotal = items.sumOf { it.price * it.quantity }
        val discount = appliedOffer.value?.let { offer ->
            if (offer.discountPercentage > 0) subtotal * offer.discountPercentage / 100 else 0.0
        } ?: 0.0
        val total = subtotal - discount

        return OrderSummary(subtotal, discount, total)
    }

    fun placeOrder(pickupAddress: String, pickupDateTime: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val orderData = OrderData(
                    userId = currentUser.value?.id ?: "",
                    items = cartItems.value ?: mutableListOf(),
                    pickupAddress = pickupAddress,
                    pickupDateTime = pickupDateTime,
                    appliedOffer = appliedOffer.value,
                    totalAmount = calculateTotal().total
                )

                val order = repository.createOrder(orderData)
                clearCart()
                loadUserOrders()
                callback(true, order.id)
            } catch (e: Exception) {
                callback(false, e.message)
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
                orders.value = repository.getUserOrders(user.id)
            }
        }
    }

    fun logout() {
        SessionManager.clear()
        currentUser.value = null
        // Reset states
        loginResult.value = LoginResult.Idle
        registerResult.value = RegisterResult.Idle
    }
}