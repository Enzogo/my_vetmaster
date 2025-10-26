package com.proyect.myvet.feedback

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.network.FeedbackApi
import com.proyect.myvet.network.FeedbackRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun SugerenciasScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var calificacion by remember { mutableStateOf(5) }
    var sugerencias by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Enviar Feedback", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Text("Calificación (1-5):", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { rating ->
                FilterChip(
                    selected = calificacion == rating,
                    onClick = { calificacion = rating },
                    label = { Text("$rating ⭐") }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = sugerencias,
            onValueChange = { sugerencias = it },
            label = { Text("Sugerencias (opcional)") },
            modifier = Modifier.fillMaxWidth().height(200.dp),
            maxLines = 8
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        val api = RetrofitClient.authed(context).create(FeedbackApi::class.java)
                        api.submitFeedback(FeedbackRequest(calificacion, sugerencias.ifBlank { null }))
                        Toast.makeText(context, "Feedback enviado. ¡Gracias!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al enviar feedback", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Enviar Feedback") }
    }
}
