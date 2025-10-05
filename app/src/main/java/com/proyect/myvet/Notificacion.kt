package com.proyect.myvet

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Notificacion : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Recibimos los datos que enviamos desde la pantalla de citas
        val mascota = intent.getStringExtra("MASCOTA_NOMBRE") ?: "Tu mascota"
        val motivo = intent.getStringExtra("MOTIVO_CITA") ?: "Recordatorio de cita"

        // Construimos la notificación que verá el usuario
        val notification = NotificationCompat.Builder(context, "citas_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de Cita: $mascota")
            .setContentText(motivo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Mostramos la notificación en el celular
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}