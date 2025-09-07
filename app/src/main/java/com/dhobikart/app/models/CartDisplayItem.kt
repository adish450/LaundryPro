package com.dhobikart.app.models

sealed class CartDisplayItem {
    data class Header(val serviceName: String) : CartDisplayItem()
    data class Item(val cartItem: CartItem) : CartDisplayItem()
    data class Footer(val subtotal: Double) : CartDisplayItem()
}