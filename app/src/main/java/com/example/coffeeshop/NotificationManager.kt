package com.example.coffeeshop.managers

import android.content.Context
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging

class NotificationManager(private val context: Context) {

    companion object {
        const val TOPIC_PROMOTIONS = "promotions"
        const val TOPIC_ORDERS = "orders"
        const val TOPIC_ALL_USERS = "all_users"
    }

    fun subscribeToTopics() {
        // Подписываемся на общие темы
        subscribeToTopic(TOPIC_PROMOTIONS) // Акции и скидки
        subscribeToTopic(TOPIC_ORDERS)    // Обновления заказов

        // Можно подписать на индивидуальную тему по userId
        val userId = getCurrentUserId()
        userId?.let {
            subscribeToTopic("user_$it")
        }
    }

    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Успешно подписались
                } else {
                    // Ошибка подписки
                }
            }
    }

    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Отписались от: $topic", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getFCMToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result)
                } else {
                    callback(null)
                }
            }
    }

    fun enableNotifications() {
        // Сохраняем настройку
        val prefs = context.getSharedPreferences("coffee_shop_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", true).apply()

        // Подписываемся на темы
        subscribeToTopics()
    }

    fun disableNotifications() {
        // Сохраняем настройку
        val prefs = context.getSharedPreferences("coffee_shop_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", false).apply()

        // Отписываемся от тем
        unsubscribeFromTopic(TOPIC_PROMOTIONS)
        unsubscribeFromTopic(TOPIC_ORDERS)
    }

    fun areNotificationsEnabled(): Boolean {
        val prefs = context.getSharedPreferences("coffee_shop_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("notifications_enabled", true)
    }

    private fun getCurrentUserId(): String? {
        val prefs = context.getSharedPreferences("coffee_shop_prefs", Context.MODE_PRIVATE)
        return prefs.getString("user_id", null)
    }
}