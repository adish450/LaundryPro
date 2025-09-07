package com.dhobikart.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dhobikart.app.models.CartItem
import com.dhobikart.app.models.Offer
import com.dhobikart.app.models.OrderSummary
import com.dhobikart.app.models.ServiceCloth

object CartManager {

    private const val CART_PREFS = "Dhobikart_Cart"
    private const val CART_ITEMS_KEY = "cart_items"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    val appliedOffer = MutableLiveData<Offer?>()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        loadCart()
    }

    private fun saveCart() {
        val editor = prefs.edit()
        val cartJson = gson.toJson(cartItems.value)
        editor.putString(CART_ITEMS_KEY, cartJson)
        editor.apply()
    }

    private fun loadCart() {
        val cartJson = prefs.getString(CART_ITEMS_KEY, null)
        val items: MutableList<CartItem> = if (cartJson != null) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            try {
                gson.fromJson(cartJson, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
        _cartItems.postValue(items) // Use postValue to update the LiveData
    }

    // This is now the primary method for adding or incrementing.
    fun addToCart(item: ServiceCloth, serviceId: String) {
        val cart = _cartItems.value ?: mutableListOf()
        // Find an item with BOTH a matching item ID AND service ID
        val existingItem = cart.find { it.itemId == item.clothId && it.serviceId == serviceId }

        if (existingItem != null) {
            // If it exists for this service, just increment the quantity
            existingItem.quantity++
        } else {
            // If it's a new item for this service, add it to the list
            cart.add(CartItem(itemId = item.clothId, name = item.name, price = item.price, quantity = 1, serviceId = serviceId))
        }
        _cartItems.value = cart
    }

    // This method is now only for decrementing or removing.
    fun removeFromCart(itemId: String, serviceId: String) {
        val cart = _cartItems.value ?: return
        val existingItem = cart.find { it.itemId == itemId && it.serviceId == serviceId }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                cart.remove(existingItem)
            }
        }
        _cartItems.value = cart
    }

    fun updateCartItemQuantity(itemId: String, serviceId: String, newQuantity: Int) {
        val cart = _cartItems.value ?: return
        val itemToUpdate = cart.find { it.itemId == itemId && it.serviceId == serviceId }

        if (itemToUpdate != null) {
            if (newQuantity > 0) {
                itemToUpdate.quantity = newQuantity
            } else {
                cart.remove(itemToUpdate)
            }
        }
        _cartItems.value = cart
    }

    fun getCartItemQuantity(itemId: String): Int {
        return cartItems.value?.find { it.itemId == itemId }?.quantity ?: 0
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }

    fun applyOffer(offer: Offer) {
        appliedOffer.postValue(offer)
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
}