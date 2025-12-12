package com.example.coffeeshop

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapters.CoffeeAdapter
import com.example.coffeeshop.databinding.ActivityMenuBinding
import com.example.coffeeshop.models.CartItem
import com.example.coffeeshop.models.CoffeeItem

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val cartItems = mutableListOf<CartItem>() // Список товаров в корзине

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        // Логируем начальное состояние
        Log.d("CartDebug", "MenuActivity создана. Корзина пуста: ${cartItems.isEmpty()}")
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Меню"
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val coffeeItems = listOf(
            CoffeeItem(1, "Эспрессо", "Классический крепкий кофе", 120.0),
            CoffeeItem(2, "Капучино", "Кофе с молочной пенкой", 180.0),
            CoffeeItem(3, "Латте", "Нежный кофе с молоком", 200.0),
            CoffeeItem(4, "Американо", "Черный кофе по-американски", 150.0),
            CoffeeItem(5, "Мокко", "Кофе с шоколадом", 220.0),
            CoffeeItem(6, "Флэт Уайт", "Австралийский вариант", 190.0),
            CoffeeItem(7, "Раф", "Кофе со сливками", 210.0),
            CoffeeItem(8, "Фильтр кофе", "Альтернативное заваривание", 170.0)
        )

        binding.coffeeRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CoffeeAdapter(coffeeItems) { coffeeItem ->
            // СОЗДАЕМ КОПИЮ перед добавлением в корзину!
            val coffeeCopy = coffeeItem.copy()
            addToCart(coffeeCopy)

            Log.d("CartDebug", "Добавлен в корзину: ${coffeeCopy.name}, ID: ${coffeeCopy.id}, Hash: ${coffeeCopy.hashCode()}")

            Toast.makeText(
                this,
                "${coffeeItem.name} добавлен в корзину за ${coffeeItem.price} ₽",
                Toast.LENGTH_SHORT
            ).show()

            // Логируем текущее состояние корзины
            logCartState()
        }
        binding.coffeeRecyclerView.adapter = adapter
    }

    private fun addToCart(coffeeItem: CoffeeItem) {
        // Проверяем, есть ли уже такой товар в корзине
        val existingItem = cartItems.find { it.coffeeItem.id == coffeeItem.id }

        if (existingItem != null) {
            // Увеличиваем количество, если товар уже есть
            existingItem.quantity++
            Log.d("CartDebug", "Увеличено количество ${coffeeItem.name}: теперь ${existingItem.quantity} шт.")
            Toast.makeText(this, "Количество ${coffeeItem.name} увеличено", Toast.LENGTH_SHORT).show()
        } else {
            // Добавляем новый товар
            cartItems.add(CartItem(coffeeItem, 1))
            Log.d("CartDebug", "Добавлен новый товар: ${coffeeItem.name}")
        }

        // Обновляем UI корзины
        updateCartUI()
    }

    private fun updateCartUI() {
        // Здесь можно обновить иконку корзины с количеством товаров
        // Например, показать badge на кнопке корзины

        // Можно передать данные в CartActivity через Intent
        // или сохранить в SharedPreferences/ViewModel
    }

    private fun logCartState() {
        Log.d("CartDebug", "=== ТЕКУЩЕЕ СОСТОЯНИЕ КОРЗИНЫ ===")
        if (cartItems.isEmpty()) {
            Log.d("CartDebug", "Корзина пуста")
        } else {
            cartItems.forEachIndexed { index, cartItem ->
                Log.d("CartDebug", "$index: ${cartItem.coffeeItem.name} (ID: ${cartItem.coffeeItem.id}, Hash: ${cartItem.coffeeItem.hashCode()}) x${cartItem.quantity}")
            }
            val totalItems = cartItems.sumOf { it.quantity }
            val totalPrice = cartItems.sumOf { it.totalPrice }
            Log.d("CartDebug", "Всего товаров: $totalItems")
            Log.d("CartDebug", "Общая сумма: $totalPrice ₽")
        }
        Log.d("CartDebug", "================================")
    }

    // Функция для передачи данных в CartActivity
    private fun prepareCartForCheckout(): ArrayList<CartItem> {
        // Создаем глубокую копию списка для передачи
        val cartCopy = ArrayList<CartItem>()
        cartItems.forEach { cartItem ->
            // Создаем копию CoffeeItem и CartItem
            val coffeeCopy = cartItem.coffeeItem.copy()
            cartCopy.add(CartItem(coffeeCopy, cartItem.quantity))
        }
        return cartCopy
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}