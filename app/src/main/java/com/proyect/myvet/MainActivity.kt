package com.proyect.myvet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.proyect.myvet.ui.theme.MyVetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // El código del canal de notificaciones no cambia.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorio de Citas"
            val descriptionText = "Canal para notificaciones de citas de MyVet"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("citas_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        setContent {
            MyVetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // --- CAMBIO AQUÍ ---
                    // En lugar de llamar a MainScreen(), ahora llamamos a nuestro gestor de navegación.
                    AppNavigation()
                }
            }
        }
    }
}