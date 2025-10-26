package com.proyect.myvet.feedback

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proyect.myvet.network.FeedbackApi
import com.proyect.myvet.network.FeedbackRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun FeedbackScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var rating by remember { mutableIntStateOf(0) }
    var suggestion by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Enviar Retroalimentación",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            "¿Cómo calificarías tu experiencia?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Star rating
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            for (i in 1..5) {
                IconButton(onClick = { rating = i }) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Estrella $i",
                        tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Text(
            "Cuéntanos tu experiencia (opcional)",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = suggestion,
            onValueChange = { suggestion = it },
            placeholder = { Text("Escribe tu sugerencia o comentario...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (rating == 0) {
                    Toast.makeText(context, "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isSubmitting = true
                scope.launch(Dispatchers.IO) {
                    try {
                        val api = RetrofitClient.authed(context).create(FeedbackApi::class.java)
                        api.submitFeedback(
                            FeedbackRequest(
                                rating = rating,
                                suggestion = suggestion.ifBlank { null }
                            )
                        )

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "¡Gracias por tu retroalimentación!", Toast.LENGTH_LONG).show()
                            // Reset form
                            rating = 0
                            suggestion = ""
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        withContext(Dispatchers.Main) {
                            if (code == 401) {
                                Toast.makeText(context, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al enviar retroalimentación: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            isSubmitting = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isSubmitting
        ) {
            Text(if (isSubmitting) "Enviando..." else "Enviar Retroalimentación")
        }
    }
}
