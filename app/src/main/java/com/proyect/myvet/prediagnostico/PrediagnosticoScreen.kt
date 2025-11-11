package com.proyect.myvet.prediagnostico

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Prediagnóstico IA", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        // Selector de mascota
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = mascotas.firstOrNull { it.id == mascotaSeleccionadaId }?.nombre
                    ?: if (mascotasLoading) "Cargando mascotas..." else "Seleccionar Mascota",
                onValueChange = {},
                label = { Text("Mascota") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                mascotas.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m.nombre ?: "(sin nombre)") },
                        onClick = {
                            mascotaSeleccionadaId = m.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            sintomas,
            { sintomas = it },
            label = { Text("Síntomas/Observaciones") },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(16.dp))

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
                        val motivo = URLEncoder.encode(r.recomendaciones, StandardCharsets.UTF_8.toString())
                        navController.navigate("${NavigationItem.Citas.route}?motivo=$motivo")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error en prediagnóstico: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Generar y Agendar") }
    }
}