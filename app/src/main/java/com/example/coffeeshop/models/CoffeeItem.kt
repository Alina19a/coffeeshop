package com.example.coffeeshop.models

data class CoffeeItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int? = null
)