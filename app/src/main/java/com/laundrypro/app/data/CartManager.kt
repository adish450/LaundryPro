package com.laundrypro.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.laundrypro.app.models.CartItem
import com.laundrypro.app.models.Offer
import com.laundrypro.app.models.OrderSummary
import com.laundrypro.app.models.ServiceCloth

object CartManager {

    private const val CART_PREFS = "LaundryProCart"
    private const val CART_ITEMS_KEY = "cart_items"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    val cartItems = MutableLiveData<MutableList<CartItem>>()
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
        if (cartJson != null) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            try {
                val items: MutableList<CartItem> = gson.fromJson(cartJson, type)
                cartItems.value = items
            } catch (e: Exception) {
                cartItems.value = mutableListOf()
            }
        } else {
            cartItems.value = mutableListOf()
        }
    }

    fun addToCart(serviceCloth: ServiceCloth, serviceId: String) {
        val currentList = cartItems.value ?: mutableListOf()
        val existingItem = currentList.find { it.itemId == serviceCloth.clothId && it.serviceId == serviceId }

        val newList = if (existingItem != null) {
            currentList.map { cartItem ->
                if (cartItem.itemId == existingItem.itemId && cartItem.serviceId == existingItem.serviceId) {
                    cartItem.copy(quantity = cartItem.quantity + 1)
                } else {
                    cartItem
                }
            }
        } else {
            currentList + CartItem(
                itemId = serviceCloth.clothId,
                name = serviceCloth.name,
                price = serviceCloth.price,
                quantity = 1,
                serviceId = serviceId
            )
        }

        cartItems.postValue(newList.toMutableList())
        saveCart()
    }

    fun removeFromCart(itemId: String, serviceId: String) {
        val currentList = cartItems.value ?: return
        val newList = currentList.filterNot { it.itemId == itemId && it.serviceId == serviceId }
        cartItems.postValue(newList.toMutableList())
        saveCart()
    }

    fun updateCartItemQuantity(itemId: String, serviceId: String, quantity: Int) {
        val currentList = cartItems.value ?: return

        val newList = if (quantity <= 0) {
            currentList.filterNot { it.itemId == itemId && it.serviceId == serviceId }
        } else {
            currentList.map { cartItem ->
                if (cartItem.itemId == itemId && cartItem.serviceId == serviceId) {
                    cartItem.copy(quantity = quantity)
                } else {
                    cartItem
                }
            }
        }
        cartItems.postValue(newList.toMutableList())
        saveCart()
    }

    fun clearCart() {
        cartItems.postValue(mutableListOf())
        appliedOffer.postValue(null)
        saveCart()
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