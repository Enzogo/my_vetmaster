package com.proyect.myvet

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notificacion : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val motivo = intent.getStringExtra("MOTIVO_CITA") ?: "Cita veterinaria"
        val channelId = "citas_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios de Citas", NotificationManager.IMPORTANCE_DEFAULT)
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio de cita")
            .setContentText(motivo)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notif)
    }
}