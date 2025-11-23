package com.proyect.myvet.vet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.network.MascotaDto
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.network.VetApi
import com.proyect.myvet.network.VetCitaDto
import com.proyect.myvet.network.VetCitaUpdateRequest
import com.proyect.myvet.network.VetProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EB))
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Tarjeta de perfil principal con avatar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA581)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar circular
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Avatar Veterinario",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (loading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        val p = data
                        if (p != null) {
                            Text(
                                text = p.nombre ?: "Veterinario",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "🩺 Veterinario Profesional",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // Información personal
        if (!loading && data != null) {
            val p = data!!

            item {
                Text(
                    "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7DA581),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRowModern(Icons.Default.Email, "Email", p.email)
                        if (!p.telefono.isNullOrBlank()) {
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRowModern(Icons.Default.Phone, "Teléfono", p.telefono)
                        }
                        if (!p.direccion.isNullOrBlank()) {
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRowModern(Icons.Default.LocationOn, "Dirección", p.direccion)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // Información de clínica
            item {
                Text(
                    "Clínica Veterinaria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7DA581),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        if (!p.clinicName.isNullOrBlank()) {
                            InfoRowModern(Icons.Default.LocalHospital, "Nombre", p.clinicName)
                        }
                        if (!p.clinicPhone.isNullOrBlank()) {
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRowModern(Icons.Default.Phone, "Teléfono", p.clinicPhone)
                        }
                        if (!p.clinicAddress.isNullOrBlank()) {
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRowModern(Icons.Default.LocationOn, "Dirección", p.clinicAddress)
                        }
                        if (!p.speciality.isNullOrBlank()) {
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRowModern(Icons.Default.Favorite, "Especialidad", p.speciality)
                        }
                        if (!p.registrationNumber.isNullOrBlank()) {
                            Divider(Modifier.padding(vertical = 8.dp))
                            InfoRowModern(Icons.Default.Badge, "Nº Registro", p.registrationNumber)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Botones de acción
            item {
                Column(Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { navController.navigate("editar_perfil") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Editar Perfil", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { authVM.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7DA581)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRowModern(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF7DA581),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
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
    var mascotas by remember { mutableStateOf<List<MascotaDto>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Colores para los campos de texto (texto negro)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedBorderColor = Color(0xFF7DA581),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFF7DA581),
        unfocusedLabelColor = Color.Gray
    )

    fun load() = scope.launch {
        loading = true
        try {
            val api = RetrofitClient.authed(context).create(VetApi::class.java)
            citas = api.citas()

            // Intentar cargar mascotas si existe el endpoint
            try {
                val ownerApi = RetrofitClient.authed(context).create(com.proyect.myvet.network.OwnerApi::class.java)
                mascotas = ownerApi.getMyMascotas()
            } catch (_: Exception) {
                // Si no se pueden cargar las mascotas, continuar sin ellas
            }
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
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Gestión de Citas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "${citas.size} cita(s) registrada(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF7DA581))
            }
        }

        if (citas.isEmpty() && !loading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No hay citas registradas",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(citas) { c ->
                var estado by remember(c.id) { mutableStateOf(c.estado ?: "pendiente") }
                var notas by remember(c.id) { mutableStateOf(c.notas ?: "") }
                var diagnostico by remember(c.id) { mutableStateOf(c.diagnostico ?: "") }
                var procedimientos by remember(c.id) { mutableStateOf(c.procedimientos ?: "") }
                var recomendaciones by remember(c.id) { mutableStateOf(c.recomendaciones ?: "") }
                var horaInicio by remember(c.id) { mutableStateOf(c.horaInicio ?: "") }
                var horaFin by remember(c.id) { mutableStateOf(c.horaFin ?: "") }
                var mascotaSeleccionadaId by remember(c.id) { mutableStateOf(c.mascotaId) }
                var expandedDetails by remember(c.id) { mutableStateOf(false) }

                val fechaStr = c.fechaIso ?: c.fecha ?: "-"
                val mascotaStr = c.mascotaNombre ?: c.mascotaId ?: "(Mascota)"
                val duenioStr = c.duenioNombre ?: c.ownerId ?: "-"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        // Encabezado de la cita con ícono
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = Color(0xFF7DA581),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        mascotaStr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        duenioStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            // Badge de estado
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (estado) {
                                    "hecha" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    "en_curso" -> Color(0xFFFFA726).copy(alpha = 0.2f)
                                    else -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    when (estado) {
                                        "hecha" -> "✓ Completada"
                                        "en_curso" -> "⏳ En curso"
                                        else -> "⏱ Pendiente"
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Divider(Modifier.padding(vertical = 12.dp))

                        // Información de fecha y motivo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF7DA581),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                fechaStr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (!c.motivo.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF7DA581),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Motivo: ${c.motivo}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }

                        // Datos del tutor
                        if (!c.duenioTelefono.isNullOrBlank() || !c.duenioCorreo.isNullOrBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFF5F1EB),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Datos del Tutor", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFF7DA581))
                                    if (!c.duenioTelefono.isNullOrBlank()) {
                                        Text("Tel: ${c.duenioTelefono}", fontSize = 11.sp, color = Color.Black)
                                    }
                                    if (!c.duenioCorreo.isNullOrBlank()) {
                                        Text("Email: ${c.duenioCorreo}", fontSize = 11.sp, color = Color.Black)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Selector de estado
                        var expandedEstado by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedEstado,
                            onExpandedChange = { expandedEstado = !expandedEstado }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = when (estado) {
                                    "en_curso" -> "En curso"
                                    "hecha" -> "Completada"
                                    else -> "Pendiente"
                                },
                                onValueChange = {},
                                label = { Text("Estado") },
                                leadingIcon = {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = textFieldColors
                            )
                            ExposedDropdownMenu(
                                expanded = expandedEstado,
                                onDismissRequest = { expandedEstado = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("⏱ Pendiente") },
                                    onClick = { estado = "pendiente"; expandedEstado = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("⏳ En curso") },
                                    onClick = { estado = "en_curso"; expandedEstado = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("✓ Completada") },
                                    onClick = { estado = "hecha"; expandedEstado = false }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Horas de atención (visible cuando se marca como completada)
                        if (estado == "hecha") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = horaInicio,
                                    onValueChange = { horaInicio = it },
                                    label = { Text("Hora Inicio") },
                                    modifier = Modifier.weight(1f),
                                    colors = textFieldColors
                                )
                                OutlinedTextField(
                                    value = horaFin,
                                    onValueChange = { horaFin = it },
                                    label = { Text("Hora Fin") },
                                    modifier = Modifier.weight(1f),
                                    colors = textFieldColors
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // Campo de notas
                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = { Text("Notas (opcional)") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 3,
                            colors = textFieldColors
                        )

                        Spacer(Modifier.height(12.dp))

                        // Botón expandir ficha médica
                        Button(
                            onClick = { expandedDetails = !expandedDetails },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F1EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (expandedDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Ficha Médica", fontWeight = FontWeight.Bold, color = Color(0xFF7DA581))
                        }

                        // Formulario expandible de ficha médica
                        if (expandedDetails) {
                            Spacer(Modifier.height(12.dp))
                            Divider()
                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = diagnostico,
                                onValueChange = { diagnostico = it },
                                label = { Text("Diagnóstico") },
                                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4,
                                colors = textFieldColors
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = procedimientos,
                                onValueChange = { procedimientos = it },
                                label = { Text("Procedimientos Realizados") },
                                leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4,
                                colors = textFieldColors
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = recomendaciones,
                                onValueChange = { recomendaciones = it },
                                label = { Text("Recomendaciones") },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4,
                                colors = textFieldColors
                            )

                            Spacer(Modifier.height(16.dp))
                        }

                        // Botón guardar
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val api = RetrofitClient.authed(context).create(VetApi::class.java)
                                        val id = c.id ?: run {
                                            Toast.makeText(context, "Id de cita no disponible", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }
                                        api.updateCita(
                                            id,
                                            VetCitaUpdateRequest(
                                                estado = estado,
                                                notas = notas.ifBlank { null },
                                                diagnostico = diagnostico.ifBlank { null },
                                                procedimientos = procedimientos.ifBlank { null },
                                                recomendaciones = recomendaciones.ifBlank { null },
                                                horaInicio = horaInicio.ifBlank { null },
                                                horaFin = horaFin.ifBlank { null }
                                            )
                                        )
                                        Toast.makeText(context, "✓ Cita actualizada", Toast.LENGTH_SHORT).show()
                                        load()
                                    } catch (e: Exception) {
                                        val code = (e as? HttpException)?.code()
                                        val msg = when (code) {
                                            401 -> "No autorizado (token)."
                                            403 -> "Solo veterinarios pueden editar."
                                            404 -> "PATCH /api/vet/citas/{id} no existe en backend."
                                            else -> "No se pudo actualizar: ${e.message}"
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}