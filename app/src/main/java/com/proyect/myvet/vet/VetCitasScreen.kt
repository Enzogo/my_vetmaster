package com.proyect.myvet.vet

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.network.VetApi
import com.proyect.myvet.network.VetProfileResponse
import com.proyect.myvet.network.VetCitaDto
import com.proyect.myvet.network.VetCitaUpdateRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

// =========== PANTALLA PERFIL VETERINARIO =============

@Composable
fun VetPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val authVM = LocalAuthViewModel.current
    var loading by remember { mutableStateOf(true) }
    var data by remember { mutableStateOf<VetProfileResponse?>(null) }

    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.authed(context).create(VetApi::class.java)
            data = api.me()
        } catch (e: Exception) {
            val code = (e as? HttpException)?.code()
            val msg = when (code) {
                401 -> "Sesión expirada. Inicia sesión."
                403 -> "Solo veterinarios."
                404 -> "Falta GET /api/vet/me en backend."
                else -> "Error al cargar perfil"
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi perfil (Veterinario)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            val p = data
            if (p == null) {
                Text("No se pudieron cargar tus datos.")
            } else {
                InfoRow("Nombre", p.nombre)
                InfoRow("Email", p.email)
                InfoRow("Teléfono", p.telefono)
                InfoRow("Dirección", p.direccion)
                Spacer(Modifier.height(12.dp))
                Text("Clínica", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                InfoRow("Nombre clínica", p.clinicName)
                InfoRow("Teléfono clínica", p.clinicPhone)
                InfoRow("Dirección clínica", p.clinicAddress)
                InfoRow("Especialidad", p.speciality)
                InfoRow("Nº de registro", p.registrationNumber)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate("editar_perfil") }, modifier = Modifier.weight(1f)) {
                    Text("Editar perfil")
                }
                Button(onClick = { authVM.logout() }, modifier = Modifier.weight(1f)) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// =========== PANTALLA CITAS VETERINARIO =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetCitasScreen() {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var citas by remember { mutableStateOf<List<VetCitaDto>>(emptyList()) }
    val scope = rememberCoroutineScope()

    fun load() = scope.launch {
        loading = true
        try {
            val api = RetrofitClient.authed(context).create(VetApi::class.java)
            citas = api.citas()
        } catch (e: Exception) {
            val code = (e as? HttpException)?.code()
            val msg = when (code) {
                401 -> "No autorizado (token). Inicia sesión nuevamente."
                403 -> "Solo veterinarios pueden ver estas citas."
                404 -> "Ruta /api/vet/citas no encontrada en backend."
                else -> "Error al cargar citas"
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(citas) { c ->
                var estado by remember(c.id) { mutableStateOf(c.estado ?: "pendiente") }
                var notas by remember(c.id) { mutableStateOf(c.notas ?: "") }
                val fechaStr = c.fechaIso ?: c.fecha ?: "-"
                val mascotaStr = c.mascotaNombre ?: c.mascotaId ?: "(Mascota)"
                val duenioStr = c.duenioNombre ?: c.ownerId ?: "-"

                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(mascotaStr, style = MaterialTheme.typography.titleMedium)
                        Text("Dueño: $duenioStr")
                        Text("Fecha: $fechaStr")
                        if (!c.motivo.isNullOrBlank()) Text("Motivo: ${c.motivo}")
                        Spacer(Modifier.height(8.dp))

                        Text("Estado", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                readOnly = true,
                                value = when (estado) {
                                    "en_curso" -> "En curso"
                                    "hecha" -> "Hecha"
                                    else -> "Pendiente"
                                },
                                onValueChange = {},
                                label = { Text("Estado") },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Pendiente") }, onClick = { estado = "pendiente"; expanded = false })
                                DropdownMenuItem(text = { Text("En curso") }, onClick = { estado = "en_curso"; expanded = false })
                                DropdownMenuItem(text = { Text("Hecha") }, onClick = { estado = "hecha"; expanded = false })
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = { Text("Notas (opcional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            scope.launch {
                                try {
                                    val api = RetrofitClient.authed(context).create(VetApi::class.java)
                                    val id = c.id ?: run {
                                        Toast.makeText(context, "Id de cita no disponible", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    api.updateCita(id, VetCitaUpdateRequest(estado = estado, notas = notas.ifBlank { null }))
                                    Toast.makeText(context, "Cita actualizada", Toast.LENGTH_SHORT).show()
                                    load()
                                } catch (e: Exception) {
                                    val code = (e as? HttpException)?.code()
                                    val msg = when (code) {
                                        401 -> "No autorizado (token)."
                                        403 -> "Solo veterinarios pueden editar."
                                        404 -> "PATCH /api/vet/citas/{id} no existe en backend."
                                        else -> "No se pudo actualizar"
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) { Text("Guardar cambios") }
                    }
                }
            }
        }
    }
}