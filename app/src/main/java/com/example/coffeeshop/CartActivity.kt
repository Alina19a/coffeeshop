// com.example.coffeeshop.CartActivity
package com.example.coffeeshop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapters.CartAdapter
import com.example.coffeeshop.databinding.ActivityCartBinding
import com.example.coffeeshop.models.CartItem
import com.example.coffeeshop.models.CartManager
import com.example.coffeeshop.models.Order
import com.example.coffeeshop.models.OrderHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализируем CartManager
        CartManager.init(this)

        setupToolbar()
        setupCartItems() // Загружаем данные из CartManager
        setupRecyclerView()
        setupButtons()
        updateTotalPrice()
        checkEmptyState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupCartItems() {
        // Вместо временных данных используем CartManager
        cartItems.addAll(CartManager.getCartItems())
    }

    private fun setupRecyclerView() {
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChanged = {
                updateTotalPrice()
                CartManager.init(this) // Обновляем данные в менеджере
            },
            onItemRemoved = {
                updateTotalPrice()
                checkEmptyState()
                CartManager.init(this) // Обновляем данные в менеджере
            }
        )
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun setupButtons() {
        binding.btnCheckout.setOnClickListener {
            checkoutOrder()
        }

        binding.btnClearCart.setOnClickListener {
            clearCart()
        }

        binding.btnGoToMenu.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.totalPrice }
        binding.totalPrice.text = "${String.format("%.2f", total)} ₽"
    }

    private fun checkEmptyState() {
        if (cartItems.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
            binding.btnCheckout.visibility = View.GONE
            binding.btnClearCart.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.btnCheckout.visibility = View.VISIBLE
            binding.btnClearCart.visibility = View.VISIBLE
        }
    }

    private fun checkoutOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста!", Toast.LENGTH_SHORT).show()
            return
        }

        val orderNotes = binding.orderNotes.text.toString()
        val total = cartItems.sumOf { it.totalPrice }

        // Сохраняем заказ
        val order = Order(
            id = System.currentTimeMillis().toInt(),
            items = cartItems.toList(),
            totalPrice = total,
            notes = orderNotes,
            date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
            status = "В обработке"
        )

        // Добавляем в историю заказов
        OrderHistory.addOrder(order)

        // Показываем подтверждение
        Toast.makeText(
            this,
            "✅ Заказ оформлен!\nСумма: ${String.format("%.2f", total)} ₽",
            Toast.LENGTH_LONG
        ).show()

        // Очищаем корзину в менеджере
        CartManager.clearCart()

        // Очищаем локальный список
        clearCart()

        // Возвращаемся на главный экран
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun clearCart() {
        cartItems.clear()
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
        checkEmptyState()
        CartManager.clearCart()
        Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}