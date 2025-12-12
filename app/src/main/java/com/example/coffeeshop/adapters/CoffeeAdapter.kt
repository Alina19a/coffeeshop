package com.example.coffeeshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.R
import com.example.coffeeshop.models.CoffeeItem

class CoffeeAdapter(
    private val coffeeItems: List<CoffeeItem>,
    private val onAddToCart: (CoffeeItem) -> Unit
) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coffeeImage: ImageView = itemView.findViewById(R.id.coffeeImage)
        val coffeeName: TextView = itemView.findViewById(R.id.coffeeName)
        val coffeeDescription: TextView = itemView.findViewById(R.id.coffeeDescription)
        val coffeePrice: TextView = itemView.findViewById(R.id.coffeePrice)
        val addButton: Button = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coffee, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffeeItem = coffeeItems[position]

        holder.coffeeName.text = coffeeItem.name
        holder.coffeeDescription.text = coffeeItem.description
        holder.coffeePrice.text = "${coffeeItem.price} ₽"

        // Устанавливаем иконку: если есть imageResId, используем его, иначе выбираем по имени
        val imageResId = coffeeItem.imageResId ?: getDefaultImageForCoffee(coffeeItem.name)
        holder.coffeeImage.setImageResource(imageResId)

        holder.addButton.setOnClickListener {
            onAddToCart(coffeeItem)
            Toast.makeText(
                holder.itemView.context,
                "${coffeeItem.name} добавлен в корзину",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Клик на всей карточке
        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Выбран: ${coffeeItem.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getDefaultImageForCoffee(name: String): Int {
        return when (name) {
            "Эспрессо" -> R.drawable.ic_coffee
            "Капучино" -> R.drawable.ic_coffee
            "Латте" -> R.drawable.ic_coffee
            "Американо" -> R.drawable.ic_coffee
            "Мокко" -> R.drawable.ic_coffee
            "Флэт Уайт" -> R.drawable.ic_coffee
            "Раф" -> R.drawable.ic_coffee
            "Фильтр кофе" -> R.drawable.ic_coffee
            else -> R.drawable.ic_coffee
        }
    }

    override fun getItemCount(): Int = coffeeItems.size
}