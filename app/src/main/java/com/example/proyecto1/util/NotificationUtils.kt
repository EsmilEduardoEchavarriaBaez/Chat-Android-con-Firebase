package com.example.proyecto1.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.proyecto1.R
import com.example.proyecto1.ui.auth.LoginActivity

object NotificationUtils {

    // Crea el canal de notificaciones de los mensajes
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                context.getString(R.string.notification_channel_id),
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // Muestra una notificación con título y texto
    fun showNotification(context: Context, title: String, body: String) {
        // Desde Android 13 hay que tener el permiso, si no se da no se muestra nada
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(Constants.NOTIFICATIONS_LOG_TAG, "No se muestra \"$title\": falta el permiso de notificaciones")
            return
        }

        createChannel(context)

        // Al tocar la notificación se abre la app (el login manda a Usuarios si hay sesión)
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Se usa la hora como id para que una notificación no reemplace a la anterior
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
        Log.d(Constants.NOTIFICATIONS_LOG_TAG, "Notificación mostrada: $title")
    }
}
