// com.example.coffeeshop.models.CartManager
package com.example.coffeeshop.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private const val PREF_NAME = "cart_prefs"
    private const val KEY_CART_ITEMS = "cart_items"

    private var _cartItems = mutableListOf<CartItem>()
    val cartItems: List<CartItem>
        get() = _cartItems.toList()

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loadCartItems()
    }

    private fun loadCartItems() {
        val json = prefs.getString(KEY_CART_ITEMS, null)
        json?.let {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            _cartItems = gson.fromJson(it, type) ?: mutableListOf()
        }
    }

    private fun saveCartItems() {
        val json = gson.toJson(_cartItems)
        prefs.edit().putString(KEY_CART_ITEMS, json).apply()
    }

    fun addToCart(coffeeItem: CoffeeItem, quantity: Int = 1) {
        val existingItem = _cartItems.find { it.coffeeItem.id == coffeeItem.id }

        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            _cartItems.add(CartItem(coffeeItem, quantity))
        }
        saveCartItems()
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        cartItem.quantity = newQuantity
        saveCartItems()
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItems.remove(cartItem)
        saveCartItems()
    }

    fun clearCart() {
        _cartItems.clear()
        saveCartItems()
    }

    fun getTotalItems(): Int {
        return _cartItems.sumOf { it.quantity }
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.totalPrice }
    }

    fun getCartItems(): List<CartItem> {
        return _cartItems.toList()
    }
}