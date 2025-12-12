package com.example.coffeeshop.models

data class CartItem(
    val coffeeItem: CoffeeItem,
    var quantity: Int = 1,
    var notes: String = ""
) {
    val totalPrice: Double
        get() = coffeeItem.price * quantity
}