package com.proyect.myvet.prediagnostico

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.proyect.myvet.network.PrediRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrediagnosticoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sintomas by remember { mutableStateOf("") }
    var mascotas by remember { mutableStateOf<List<MascotaDto>>(emptyList()) }
    var mascotaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var mascotasLoading by remember { mutableStateOf(false) }

    // Colores para campos de texto (texto negro completo)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedBorderColor = Color(0xFF7DA581),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFF7DA581),
        unfocusedLabelColor = Color.Gray,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray
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
            .padding(16.dp)
    ) {
        // Encabezado
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA581)),
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
                    color = Color(0xFF7DA581)
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
                        label = { Text("Mascota") },
                        leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = textFieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (mascotas.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay mascotas registradas") },
                                onClick = { expanded = false }
                            )
                        } else {
                            mascotas.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.nombre ?: "(sin nombre)", color = Color.Black) },
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
                    color = Color(0xFF7DA581)
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

                scope.launch {
                    try {
                        val api = RetrofitClient.authed(context).create(PrediagnosticoApi::class.java)
                        val r = api.predi(
                            PrediRequest(
                                sintomas,
                                mascotaSeleccionada.especie,
                                mascotaSeleccionada.fechaNacimiento,
                                mascotaSeleccionada.sexo
                            )
                        )
                        Toast.makeText(context, "✓ Análisis completado", Toast.LENGTH_SHORT).show()
                        val motivo = URLEncoder.encode(r.recomendaciones, StandardCharsets.UTF_8.toString())
                        navController.navigate("${NavigationItem.Citas.route}?motivo=$motivo")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error en prediagnóstico: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Generar Prediagnóstico y Agendar", fontWeight = FontWeight.Bold)
        }
    }
}