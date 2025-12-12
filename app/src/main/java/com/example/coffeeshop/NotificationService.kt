package com.example.coffeeshop.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.coffeeshop.MainActivity
import com.example.coffeeshop.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "coffee_shop_channel"
        const val CHANNEL_NAME = "Coffee Shop Notifications"
        const val ORDER_TOPIC = "orders"
        const val PROMOTIONS_TOPIC = "promotions"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Проверяем наличие данных уведомления
        remoteMessage.notification?.let { notification ->
            sendNotification(
                notification.title ?: "Coffee Shop",
                notification.body ?: "Новое уведомление",
                remoteMessage.data
            )
        }

        // Также можно обрабатывать data messages
        if (remoteMessage.data.isNotEmpty()) {
            val orderId = remoteMessage.data["orderId"]
            val status = remoteMessage.data["status"]

            orderId?.let { id ->
                status?.let { st ->
                    // Обновляем статус заказа локально
                    updateLocalOrderStatus(id, st)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Отправьте токен на ваш сервер
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Здесь можно отправить токен на ваш backend
        // Например, через Retrofit или Volley
    }

    private fun sendNotification(title: String, message: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Добавляем данные из уведомления
        data.forEach { (key, value) ->
            intent.putExtra(key, value)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал для Android Oreo и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления от Coffee Shop"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun updateLocalOrderStatus(orderId: String, newStatus: String) {
        // Здесь можно обновить локальную базу данных или SharedPreferences
        // с новым статусом заказа
    }
}