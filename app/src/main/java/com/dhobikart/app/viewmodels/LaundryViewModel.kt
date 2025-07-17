package com.dhobikart.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dhobikart.app.data.CartManager
import com.dhobikart.app.data.SessionManager
import com.dhobikart.app.models.*
import com.dhobikart.app.repository.LaundryRepository
import com.dhobikart.app.models.Address
import kotlinx.coroutines.launch

class LaundryViewModel : ViewModel() {
    private val repository = LaundryRepository()

    val loginResult = MutableLiveData<LoginResult>(LoginResult.Idle)
    val registerResult = MutableLiveData<RegisterResult>(RegisterResult.Idle)
    val currentUser = MutableLiveData<User?>()
    val offers = MutableLiveData<List<Offer>>()

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    // Delegate all cart-related LiveData and functions to the singleton CartManager
    val cartItems = CartManager.cartItems
    val appliedOffer = CartManager.appliedOffer

    // LiveData to signal navigation events
    val navigateToHome = MutableLiveData<Boolean>()

    private val _placeOrderResult = MutableLiveData<PlaceOrderResult>()
    val placeOrderResult: LiveData<PlaceOrderResult> = _placeOrderResult

    // This LiveData will now hold the list of clothes with their prices included
    private val _serviceCloths = MutableLiveData<List<ServiceCloth>>()
    val serviceCloths: LiveData<List<ServiceCloth>> = _serviceCloths

    // NEW: LiveData to hold the grouped list for the checkout screen
    val groupedCartItems = MediatorLiveData<List<GroupedCartItems>>()

    // LiveData to hold the list of unique previous addresses
    val previousAddresses: LiveData<List<Address>> = _orders.map { orders ->
        orders.mapNotNull { it.pickupAddress }
            .distinct()
    }

    private val _updateProfileResult = MutableLiveData<UpdateProfileResult>(UpdateProfileResult.Idle)
    val updateProfileResult: LiveData<UpdateProfileResult> = _updateProfileResult

    init {
        loadServices()
        loadOffers()
        checkUserSession()
        setupGroupedCartObserver()
    }

    private fun setupGroupedCartObserver() {
        // This will automatically re-calculate the grouped list whenever the cart or services change
        groupedCartItems.addSource(cartItems) { updateGroupedCart() }
        groupedCartItems.addSource(services) { updateGroupedCart() }
    }

    private fun updateGroupedCart() {
        val items = cartItems.value ?: return
        val allServices = services.value ?: return

        val grouped = items.groupBy { it.serviceId }
            .map { (serviceId, cartItemsForService) ->
                val serviceName = allServices.find { it.id == serviceId }?.name ?: "Unknown Service"
                GroupedCartItems(serviceName, cartItemsForService)
            }
        groupedCartItems.value = grouped
    }

    /*private fun loadServices() {
        viewModelScope.launch {
            services.value = repository.getServices()
        }
    }*/

    fun loadServices() {
        viewModelScope.launch {
            try {
                _services.value = repository.getServices()
            } catch (e: Exception) {
                // Handle the error, e.g., show a message to the user
            }
        }
    }

    private fun loadOffers() {
        viewModelScope.launch {
            offers.value = repository.getOffers()
        }
    }

    fun loadItemsForService(serviceId: String) {
        viewModelScope.launch {
            try {
                // The logic is now much simpler, with only one repository call
                val items = repository.getServiceWithClothes(serviceId)
                _serviceCloths.postValue(items)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    /**
     * Checks SharedPreferences for a saved user session and updates currentUser.
     * This is public so MainActivity can call it in onResume.
     */
    fun checkUserSession() {
        val savedUser = SessionManager.getUser()
        if (currentUser.value?.id != savedUser?.id) {
            currentUser.value = savedUser
            // If a user is found, load their orders
            if (savedUser != null) {
                loadUserOrders()
            }
        }
    }


    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginResult.value = LoginResult.Loading
            try {
                val response = repository.login(email, password)
                SessionManager.saveLoginDetails(response.token, response.user)
                currentUser.value = response.user // Update state
                // Load orders right after setting the current user
                loadUserOrders()
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

    fun addToCart(serviceCloth: ServiceCloth, serviceId: String) {
        // We now pass the serviceCloth object to the CartManager
        CartManager.addToCart(serviceCloth, serviceId)
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
                val user = currentUser.value ?: throw Exception("User not logged in")
                val items = cartItems.value ?: emptyList()
                val total = calculateTotal().total

                val order = repository.placeOrder(user, items, total, pickupAddress)

                CartManager.clearCart()
                _placeOrderResult.value = PlaceOrderResult.Success(order)
            } catch (e: Exception) {
                _placeOrderResult.value = PlaceOrderResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onOrderPlacementHandled() {
        _placeOrderResult.value = PlaceOrderResult.Idle
    }

    fun updateUserProfile(name: String, email: String, phone: String) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address ?: emptyList()
        updateUserProfile(name, email, phone, currentAddresses)
    }

    fun updateUserProfile(name: String, email: String, phone: String, addresses: List<Address>) {
        viewModelScope.launch {
            _updateProfileResult.value = UpdateProfileResult.Loading
            try {
                val updatedUser = repository.updateUserProfile(name, email, phone, addresses)
                val currentToken = SessionManager.getToken()
                if (currentToken != null) {
                    SessionManager.saveLoginDetails(currentToken, updatedUser)
                    currentUser.value = updatedUser
                }
                _updateProfileResult.value = UpdateProfileResult.Success(updatedUser)
            } catch (e: Exception) {
                _updateProfileResult.value = UpdateProfileResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun addAddress(newAddress: Address) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address?.toMutableList() ?: mutableListOf()
        currentAddresses.add(newAddress)
        updateUserProfile(user.name ?: "", user.email ?: "", user.phone ?: "", currentAddresses)
    }

    fun updateAddress(oldAddress: Address, newAddress: Address) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address?.toMutableList() ?: mutableListOf()
        val index = currentAddresses.indexOf(oldAddress)
        if (index != -1) {
            currentAddresses[index] = newAddress
            updateUserProfile(user.name ?: "", user.email ?: "", user.phone ?: "", currentAddresses)
        }
    }

    fun deleteAddress(addressToDelete: Address) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address?.toMutableList() ?: mutableListOf()
        currentAddresses.remove(addressToDelete)
        updateUserProfile(user.name ?: "", user.email ?: "", user.phone ?: "", currentAddresses)
    }

    fun onUpdateProfileHandled() {
        _updateProfileResult.value = UpdateProfileResult.Idle
    }

    private fun clearCart() {
        cartItems.value = mutableListOf()
        appliedOffer.value = null
    }

    fun loadUserOrders() {
        val userId = currentUser.value?.id ?: return
        viewModelScope.launch {
            try {
                // This call remains the same and will now receive the correct list
                val userOrders = repository.getUserOrders(userId)
                _orders.postValue(userOrders)
            } catch (e: Exception) {
                // Handle error
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
        _orders.value = emptyList() // This is the fix: Clear the orders list
        // Trigger the navigation event
        navigateToHome.value = true
    }

    // Call this after the navigation is handled to prevent re-triggering
    fun onHomeNavigationComplete() {
        navigateToHome.value = false
    }
}