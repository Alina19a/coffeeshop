// com.example.coffeeshop.adapters.CartAdapter
package com.example.coffeeshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshop.databinding.ItemCartBinding
import com.example.coffeeshop.models.CartItem
import com.example.coffeeshop.models.CartManager

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChanged: () -> Unit,
    private val onItemRemoved: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.binding.tvCoffeeName.text = cartItem.coffeeItem.name
        holder.binding.tvCoffeeDescription.text = cartItem.coffeeItem.description
        holder.binding.tvPrice.text = "%.2f ₽".format(cartItem.coffeeItem.price)
        holder.binding.tvQuantity.text = cartItem.quantity.toString()
        holder.binding.tvItemTotal.text = "%.2f ₽".format(cartItem.totalPrice)

        // Загрузка изображения (если есть)
        // Glide.with(holder.itemView.context)
        //     .load(cartItem.coffeeItem.imageUrl)
        //     .into(holder.binding.ivCoffeeImage)

        // Обновите обработчики кнопок
        holder.binding.btnMinus.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                CartManager.updateQuantity(cartItem, cartItem.quantity)
                updateTotalPrice()
                notifyItemChanged(position)
                onQuantityChanged()
            }
        }

        holder.binding.btnPlus.setOnClickListener {
            cartItem.quantity++
            CartManager.updateQuantity(cartItem, cartItem.quantity)
            updateTotalPrice()
            notifyItemChanged(position)
            onQuantityChanged()
        }

        holder.binding.btnRemove.setOnClickListener {
            CartManager.removeFromCart(cartItem)
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size - position)
            updateTotalPrice()
            onItemRemoved()
        }
    }

    override fun getItemCount() = cartItems.size

    private fun updateTotalPrice() {
        // Обновление общей суммы в адаптере
        // Можно удалить, если используете только в активности
    }
}