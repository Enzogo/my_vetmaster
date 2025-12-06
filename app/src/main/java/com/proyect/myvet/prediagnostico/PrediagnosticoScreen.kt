package com.proyect.myvet.prediagnostico

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.NavigationItem
import com.proyect.myvet.network.MascotaDto
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.PrediagnosticoApi
import com.proyect.myvet.network.PrediagnosticoRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Función para limpiar y parsear JSON de la IA
fun limpiarTextoJSON(texto: String): String {
  try {
    // Primero elimina caracteres de escape
    var limpio = texto.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")

    // Intenta parsear como JSON
    if (limpio.startsWith("{") || limpio.startsWith("[")) {
      val json = JSONObject(limpio)
      return json.toString(2) // Formato indentado
    }

    return limpio
  } catch (e: Exception) {
    // Si no es JSON válido, solo retorna el texto limpio
    return texto.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")
  }
}

// Función para extraer contenido legible de un string JSON
fun formatearRespuestaIA(texto: String): String {
  return try {
    // Solo limpiar escapes básicos de caracteres
    texto
      .replace("\\n", "\n")
      .replace("\\\"", "\"")
      .replace("\\\\", "\\")
      .trim()
  } catch (e: Exception) {
    texto
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrediagnosticoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sintomas by remember { mutableStateOf("") }
    var mascotas by remember { mutableStateOf<List<MascotaDto>>(emptyList()) }
    var mascotaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var mascotasLoading by remember { mutableStateOf(false) }

    // Estados para la respuesta del prediagnóstico
    var loading by remember { mutableStateOf(false) }
    var recomendaciones by remember { mutableStateOf<String?>(null) }
    var redFlags by remember { mutableStateOf<String?>(null) }
    var disclaimer by remember { mutableStateOf<String?>(null) }
    var modelUsed by remember { mutableStateOf<String?>(null) }
    var urgencia by remember { mutableStateOf<String?>(null) }
    var causasFrecuentes by remember { mutableStateOf<List<String>>(emptyList()) }

    // Color principal compartido con la pestaña IA
    val mainGreen = Color(0xFF7DA581)

    // Colores para campos de texto (texto negro completo)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedBorderColor = mainGreen,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = mainGreen,
        unfocusedLabelColor = Color.Gray,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray
    )

    // Colores específicos para dropdown en selector (texto verde como en IA)
    val dropdownColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = mainGreen,
        unfocusedTextColor = mainGreen,
        cursorColor = mainGreen,
        focusedBorderColor = mainGreen,
        unfocusedBorderColor = mainGreen.copy(alpha = 0.5f),
        focusedLabelColor = mainGreen,
        unfocusedLabelColor = mainGreen
    )

    // Cargar mascotas del usuario
    LaunchedEffect(Unit) {
        mascotasLoading = true
        try {
            val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
            mascotas = api.getMyMascotas()
        } catch (_: Exception) {
            Toast.makeText(context, "No se pudieron cargar mascotas", Toast.LENGTH_SHORT).show()
        } finally {
            mascotasLoading = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Encabezado
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = mainGreen),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Prediagnóstico IA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "Asistente virtual veterinario",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selector de mascota
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Selecciona tu mascota",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = mainGreen
                )
                Spacer(Modifier.height(12.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = mascotas.firstOrNull { it.id == mascotaSeleccionadaId }?.nombre
                            ?: if (mascotasLoading) "Cargando mascotas..." else "Seleccionar Mascota",
                        onValueChange = {},
                        label = { Text("Mascota", color = mainGreen) },
                        leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null, tint = mainGreen) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = dropdownColors
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (mascotas.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay mascotas registradas", color = mainGreen) },
                                onClick = { expanded = false }
                            )
                        } else {
                            mascotas.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.nombre ?: "(sin nombre)", color = mainGreen) },
                                    onClick = {
                                        mascotaSeleccionadaId = m.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Campo de síntomas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Describe los síntomas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = mainGreen
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = sintomas,
                    onValueChange = { sintomas = it },
                    label = { Text("Síntomas/Observaciones") },
                    placeholder = { Text("Ej: Mi mascota tiene tos, fiebre y no quiere comer...") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = textFieldColors,
                    minLines = 6,
                    maxLines = 8
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Nota informativa
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "La IA analizará los síntomas y te sugerirá agendar una cita si es necesario",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botón de acción
        Button(
            onClick = {
                val mascotaSeleccionada = mascotas.firstOrNull { it.id == mascotaSeleccionadaId }
                if (mascotaSeleccionada == null) {
                    Toast.makeText(context, "Por favor selecciona una mascota", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (sintomas.isBlank()) {
                    Toast.makeText(context, "Por favor describe los síntomas", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Resetear resultados anteriores
                loading = true
                recomendaciones = null
                redFlags = null
                disclaimer = null
                modelUsed = null
                urgencia = null
                causasFrecuentes = emptyList()

                scope.launch {
                    try {
                        val api = RetrofitClient.authed(context).create(PrediagnosticoApi::class.java)

                        // Construir contexto con información de la mascota
                        val contexto = buildString {
                            append("Mascota: ${mascotaSeleccionada.nombre ?: "Sin nombre"}")
                            mascotaSeleccionada.especie?.let { append(", Especie: $it") }
                            mascotaSeleccionada.fechaNacimiento?.let { append(", Edad/FechaNac: $it") }
                            mascotaSeleccionada.sexo?.let { append(", Sexo: $it") }
                            mascotaSeleccionada.raza?.let { append(", Raza: $it") }
                        }

                        val req = PrediagnosticoRequest(
                            sintomas = sintomas.trim(),
                            especie = mascotaSeleccionada.especie,
                            edad = mascotaSeleccionada.fechaNacimiento,
                            contexto = contexto
                        )
                        val resp = api.getPrediagnostico(req)

                        if (resp.isSuccessful) {
                            try {
                                val body = resp.body()

                                if (body != null) {
                                    val parsed = body.parsed

                                    if (parsed != null && !parsed.recomendaciones.isNullOrBlank()) {
                                        recomendaciones = parsed.recomendaciones
                                        redFlags = parsed.red_flags
                                        disclaimer = parsed.disclaimer
                                        urgencia = parsed.urgencia
                                        causasFrecuentes = parsed.causas_frecuentes ?: emptyList()
                                        modelUsed = parsed.urgencia
                                        Toast.makeText(context, "✓ Análisis completado", Toast.LENGTH_SHORT).show()
                                    } else if (parsed != null) {
                                        recomendaciones = "Se generó el análisis pero sin recomendaciones específicas. Por favor consulta con un veterinario."
                                        redFlags = parsed.red_flags
                                        disclaimer = parsed.disclaimer
                                        urgencia = parsed.urgencia
                                        causasFrecuentes = parsed.causas_frecuentes ?: emptyList()
                                        Toast.makeText(context, "⚠ Análisis generado con información limitada", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Intentar obtener del body.raw
                                        recomendaciones = body.raw ?: "No se pudo procesar la respuesta. Intenta de nuevo."
                                        Toast.makeText(context, "⚠ Respuesta sin estructura completa", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Body nulo
                                    recomendaciones = "Error: respuesta vacía del servidor"
                                    Toast.makeText(context, "⚠ Respuesta vacía del servidor", Toast.LENGTH_SHORT).show()
                                }
                            } catch (parseException: Exception) {
                                recomendaciones = "Error al procesar respuesta: ${parseException.message}"
                                Toast.makeText(context, "❌ Error: ${parseException.message}", Toast.LENGTH_SHORT).show()
                                android.util.Log.e("PrediagnosticoScreen", "Error parsing response", parseException)
                            }
                        } else {
                            val code = resp.code()
                            val errorBody = resp.errorBody()?.string() ?: "Sin detalles"
                            val msg = when (code) {
                                401 -> "Sesión expirada. Inicia sesión de nuevo."
                                400 -> "Petición inválida."
                                503 -> "Servicio no disponible. Intenta de nuevo."
                                else -> "Error $code al solicitar prediagnóstico."
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            android.util.Log.e("PrediagnosticoScreen", "Error HTTP $code: $errorBody")
                        }
                    } catch (e: Exception) {
                        val msg = when (e) {
                            is HttpException -> "Error HTTP ${e.code()}"
                            else -> "Error de conexión: ${e.message}"
                        }
                        recomendaciones = msg
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        android.util.Log.e("PrediagnosticoScreen", "Exception", e)
                    } finally {
                        loading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = mainGreen),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Consultando IA...", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generar Prediagnóstico", fontWeight = FontWeight.Bold)
            }
        }

        // Mostrar resultados si existen
        if (recomendaciones != null || redFlags != null) {
            Spacer(Modifier.height(16.dp))

            // Tarjeta de Recomendaciones
            recomendaciones?.let { reco ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Recomendaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            formatearRespuestaIA(reco),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Tarjeta de Red Flags (si existen)
            redFlags?.let { flags ->
                if (flags.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Señales de Alerta",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                formatearRespuestaIA(flags),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Tarjeta de Causas Frecuentes (si existen)
            if (causasFrecuentes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = null,
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Causas Frecuentes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        causasFrecuentes.forEach { causa ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "•",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF7B1FA2),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    causa,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Tarjeta de Disclaimer
            disclaimer?.let { disc ->
                if (disc.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB74D),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Aviso Legal",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                formatearRespuestaIA(disc),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // Mostrar confianza del análisis (opcional)
            modelUsed?.let { confidence ->
                if (confidence.isNotBlank()) {
                    Text(
                        "Nivel de confianza: $confidence",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botón para agendar cita con las recomendaciones
            Button(
                onClick = {
                    val motivo = URLEncoder.encode(
                        recomendaciones ?: "Consulta recomendada por IA",
                        StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate("${NavigationItem.Citas.route}?motivo=$motivo")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = mainGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agendar Cita Veterinaria", fontWeight = FontWeight.Bold)
            }
        }
    }
}
