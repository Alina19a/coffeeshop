package com.example.coffeeshop.models

data class Order(
    val id: Int,
    val items: List<CartItem>,
    val totalPrice: Double,
    val notes: String,
    val date: String,
    val status: String // "В обработке", "Готовится", "Готов", "Выполнен"
)

object OrderHistory {
    private val orders = mutableListOf<Order>()

    fun addOrder(order: Order) {
        orders.add(0, order) // Добавляем в начало списка
    }

    fun getOrders(): List<Order> = orders

    fun clearHistory() {
        orders.clear()
    }
}