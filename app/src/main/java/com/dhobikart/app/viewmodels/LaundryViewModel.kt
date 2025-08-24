package com.dhobikart.app.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.dhobikart.app.data.CartManager
import com.dhobikart.app.data.SessionManager
import com.dhobikart.app.models.*
import com.dhobikart.app.repository.LaundryRepository
import kotlinx.coroutines.launch

class LaundryViewModel : ViewModel() {
    private val TAG = "LaundryViewModel"
    private val repository = LaundryRepository()

    val loginResult = MutableLiveData<LoginResult>(LoginResult.Idle)
    val registerResult = MutableLiveData<RegisterResult>(RegisterResult.Idle)
    val navigateToTab = MutableLiveData<Int?>()
    val currentUser = MutableLiveData<User?>()
    val offers = MutableLiveData<List<Offer>>()
    val forgotPasswordOtpSent = MutableLiveData<Boolean>()
    val passwordResetSuccessful = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()

    private val _services = MutableLiveData<List<LaundryService>>()
    val services: LiveData<List<LaundryService>> = _services

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    val previousAddresses: LiveData<List<Address>> = _orders.map { orders ->
        orders.mapNotNull { it.pickupAddress }
            .distinct()
    }
    private val _updateProfileResult = MutableLiveData<UpdateProfileResult>(UpdateProfileResult.Idle)
    val updateProfileResult: LiveData<UpdateProfileResult> = _updateProfileResult

    private val _addressListUpdated = MutableLiveData<Event<User>>()
    val addressListUpdated: LiveData<Event<User>> = _addressListUpdated

    val cartItems = CartManager.cartItems
    val appliedOffer = CartManager.appliedOffer

    val navigateToHome = MutableLiveData<Boolean>()

    private val _placeOrderResult = MutableLiveData<PlaceOrderResult>()
    val placeOrderResult: LiveData<PlaceOrderResult> = _placeOrderResult

    private val _pricedClothItems = MutableLiveData<List<PricedClothItem>>()
    val pricedClothItems: LiveData<List<PricedClothItem>> = _pricedClothItems

    private val _serviceCloths = MutableLiveData<List<ServiceCloth>>()
    val serviceCloths: LiveData<List<ServiceCloth>> = _serviceCloths

    val groupedCartItems = MediatorLiveData<List<GroupedCartItems>>()

    init {
        loadServices()
        loadOffers()
        checkUserSession()
        setupGroupedCartObserver()
    }

    private fun setupGroupedCartObserver() {
        groupedCartItems.addSource(cartItems) { updateGroupedCart() }
        groupedCartItems.addSource(services) { updateGroupedCart() }
    }

    private fun updateGroupedCart() {
        val items = cartItems.value ?: return
        val allServices = services.value ?: return

        val grouped = items.groupBy { it.serviceId }
            .map { (serviceId, cartItemsForService) ->
                val serviceName = allServices.find { it.id.toString() == serviceId }?.name ?: "Unknown Service"
                GroupedCartItems(serviceName, cartItemsForService)
            }
        groupedCartItems.value = grouped
    }

    fun loadServices() {
        viewModelScope.launch {
            try {
                Log.v(TAG, "Fetching services from server...")
                val servicesResult = repository.getServices()
                _services.postValue(servicesResult)
                Log.v(TAG, "Successfully fetched ${servicesResult.size} services.")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching services: ${e.message}", e)
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
                _serviceCloths.postValue(repository.getServiceWithClothes(serviceId))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun checkUserSession() {
        val savedUser = SessionManager.getUser()
        if (currentUser.value?.id != savedUser?.id) {
            currentUser.value = savedUser
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
                currentUser.value = response.user
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
                SessionManager.saveLoginDetails(response.token, response.user)
                currentUser.value = response.user
                registerResult.value = RegisterResult.Success(response.user)
                loginResult.value = LoginResult.Success(response.user)
            } catch (e: Exception) {
                registerResult.value = RegisterResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun requestOtp(email: String) {
        viewModelScope.launch {
            try {
                repository.requestOtp(email)
                forgotPasswordOtpSent.postValue(true)
            } catch (e: Exception) {
                error.postValue(e.message)
            }
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            try {
                repository.resetPassword(email, otp, newPassword)
                passwordResetSuccessful.postValue(true)
            } catch (e: Exception) {
                error.postValue(e.message)
            }
        }
    }

    fun addToCart(serviceCloth: ServiceCloth, serviceId: String) {
        CartManager.addToCart(serviceCloth, serviceId)
    }

    fun removeFromCart(itemId: String, serviceId: String) {
        CartManager.removeFromCart(itemId, serviceId)
    }

    fun updateCartItemQuantity(itemId: String, serviceId: String, quantity: Int) {
        CartManager.updateCartItemQuantity(itemId, serviceId, quantity)
    }

    fun applyOffer(offerCode: String): Boolean {
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

    fun updateUserProfile(name: String, email: String, phone: String, addresses: List<Address>) {
        viewModelScope.launch {
            _updateProfileResult.value = UpdateProfileResult.Loading
            try {
                val updatedUser = repository.updateUserProfile(name, email, phone, addresses)
                val currentToken = SessionManager.getToken()
                if (currentToken != null) {
                    SessionManager.saveLoginDetails(currentToken, updatedUser)
                    currentUser.value = updatedUser
                    // **THE FIX:** The event now contains the updated user object.
                    _addressListUpdated.value = Event(updatedUser)
                }
                _updateProfileResult.value = UpdateProfileResult.Success(updatedUser)
            } catch (e: Exception) {
                _updateProfileResult.value = UpdateProfileResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    /**
     * This is a new, private function specifically for updating addresses.
     * It updates the user but does NOT set the _updateProfileResult LiveData.
     */
    private fun updateUserAddressesOnly(name: String, email: String, phone: String, addresses: List<Address>) {
        viewModelScope.launch {
            try {
                val updatedUser = repository.updateUserProfile(name, email, phone, addresses)
                val currentToken = SessionManager.getToken()
                if (currentToken != null) {
                    SessionManager.saveLoginDetails(currentToken, updatedUser)
                    currentUser.value = updatedUser
                    _addressListUpdated.value = Event(updatedUser)
                }
            } catch (e: Exception) {
                error.value = "Failed to update address: ${e.message}"
            }
        }
    }

    fun addAddress(newAddress: Address) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address?.toMutableList() ?: mutableListOf()
        currentAddresses.add(newAddress)
        updateUserAddressesOnly(user.name ?: "", user.email ?: "", user.phone ?: "", currentAddresses.distinct())
    }

    fun updateAddress(oldAddress: Address, newAddress: Address) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address?.toMutableList() ?: mutableListOf()
        val index = currentAddresses.indexOf(oldAddress)
        if (index != -1) {
            currentAddresses[index] = newAddress
            updateUserAddressesOnly(user.name ?: "", user.email ?: "", user.phone ?: "", currentAddresses.distinct())
        }
    }

    fun deleteAddress(addressToDelete: Address) {
        val user = currentUser.value ?: return
        val currentAddresses = user.address?.toMutableList() ?: mutableListOf()
        currentAddresses.remove(addressToDelete)
        updateUserAddressesOnly(user.name ?: "", user.email ?: "", user.phone ?: "", currentAddresses.distinct())
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
        loginResult.value = LoginResult.Idle
        registerResult.value = RegisterResult.Idle
        _orders.value = emptyList()
        navigateToHome.value = true
    }

    fun onHomeNavigationComplete() {
        navigateToHome.value = false
    }
}
