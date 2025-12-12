package com.example.coffeeshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.R
import com.example.coffeeshop.models.Order

class OrdersAdapter(
    private val orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderId)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val orderTotal: TextView = itemView.findViewById(R.id.orderTotal)
        val orderItemsCount: TextView = itemView.findViewById(R.id.orderItemsCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.orderId.text = "Заказ #${order.id}"
        holder.orderDate.text = order.date
        holder.orderStatus.text = order.status
        holder.orderTotal.text = "${String.format("%.2f", order.totalPrice)} ₽"
        holder.orderItemsCount.text = "${order.items.size} позиций"

        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size
}