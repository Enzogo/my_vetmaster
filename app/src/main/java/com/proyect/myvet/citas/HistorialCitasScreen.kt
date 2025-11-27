package com.proyect.myvet.citas

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyect.myvet.network.CitaDto
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialCitasScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var citasPendientes by remember { mutableStateOf<List<CitaDto>>(emptyList()) }
    var citasCompletadas by remember { mutableStateOf<List<CitaDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val pullToRefreshState = rememberPullToRefreshState()

    fun loadCitas() {
        isLoading = true
        scope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                citasPendientes = api.getCitasPendientes()
                citasCompletadas = api.getCitasCompletadas()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✓ Citas actualizadas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error cargando citas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCitas()
        while (true) {
            kotlinx.coroutines.delay(30000)
            loadCitas()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EB))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA581)),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Historial de Citas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Desliza hacia abajo para actualizar", style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.7f))
                    }
                }
                IconButton(onClick = { loadCitas() }, enabled = !isLoading) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.Black)
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF7DA581),
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Pendientes (${citasPendientes.size})")
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Completadas (${citasCompletadas.size})")
                    }
                }
            )
        }

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { loadCitas() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (selectedTab == 0) {
                    if (citasPendientes.isEmpty()) {
                        EmptyStateCard("No hay citas pendientes")
                    } else {
                        citasPendientes.forEach { cita ->
                            TarjetaCitaPendiente(cita)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                } else {
                    if (citasCompletadas.isEmpty()) {
                        EmptyStateCard("No hay citas completadas")
                    } else {
                        citasCompletadas.forEach { cita ->
                            TarjetaCitaCompletada(cita)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
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
                message,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TarjetaCitaPendiente(cita: CitaDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Tutor: ${cita.duenioNombre ?: "N/A"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        cita.motivo ?: "Sin especificar",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text("PENDIENTE", color = Color.White, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFFF9800)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(cita.fechaIso ?: "", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mascota: ${cita.nombreMascota ?: "N/A"}", fontSize = 12.sp, color = Color.Black)
            }

            if (!cita.duenioTelefono.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tel: ${cita.duenioTelefono}", fontSize = 11.sp, color = Color.Gray)
                }
            }

            if (!cita.duenioCorreo.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Email: ${cita.duenioCorreo}", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun TarjetaCitaCompletada(cita: CitaDto) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var horaInicio by remember { mutableStateOf(cita.horaInicio ?: "") }
    var horaFin by remember { mutableStateOf(cita.horaFin ?: "") }
    var isEditingTime by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Tutor: ${cita.duenioNombre ?: "N/A"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        "Atendido por: ${cita.veterinarioNombre ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text("COMPLETADA", color = Color.White, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(cita.fechaIso ?: "", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Ficha Técnica
            if (!cita.diagnostico.isNullOrBlank() || !cita.procedimientos.isNullOrBlank() || !cita.recomendaciones.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("📋 Ficha Técnica", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF7DA581))

                        if (!cita.diagnostico.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Diagnóstico:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Black)
                            Text(cita.diagnostico ?: "", fontSize = 10.sp, color = Color.Gray)
                        }

                        if (!cita.procedimientos.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Procedimientos:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Black)
                            Text(cita.procedimientos ?: "", fontSize = 10.sp, color = Color.Gray)
                        }

                        if (!cita.recomendaciones.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Recomendaciones:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Black)
                            Text(cita.recomendaciones ?: "", fontSize = 10.sp, color = Color.Gray)
                        }

                        if (!cita.notas.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Notas:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.Black)
                            Text(cita.notas ?: "", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (isEditingTime) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F1EB)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Registrar Hora de Atención", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF7DA581))
                        Spacer(Modifier.height(8.dp))

                        val calendar = remember { Calendar.getInstance() }
                        val timePickerDialogInicio = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val cal = Calendar.getInstance()
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                cal.set(Calendar.MINUTE, minute)
                                horaInicio = tf.format(cal.time)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )

                        val timePickerDialogFin = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val cal = Calendar.getInstance()
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                cal.set(Calendar.MINUTE, minute)
                                horaFin = tf.format(cal.time)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { timePickerDialogInicio.show() },
                                modifier = Modifier.weight(1f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(if (horaInicio.isEmpty()) "Inicio" else horaInicio, fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { timePickerDialogFin.show() },
                                modifier = Modifier.weight(1f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(if (horaFin.isEmpty()) "Fin" else horaFin, fontSize = 11.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                                            val updateRequest = com.proyect.myvet.network.CitaUpdateRequest(
                                                horaInicio = horaInicio,
                                                horaFin = horaFin
                                            )
                                            api.updateCita(cita.id ?: return@launch, updateRequest)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "✓ Horario guardado", Toast.LENGTH_SHORT).show()
                                                isEditingTime = false
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                            ) {
                                Text("Guardar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    horaInicio = cita.horaInicio ?: ""
                                    horaFin = cita.horaFin ?: ""
                                    isEditingTime = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                            ) {
                                Text("Cancelar", fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (horaInicio.isNotEmpty() || horaFin.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEditingTime = !isEditingTime }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("$horaInicio - $horaFin", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(16.dp))
                }
            } else {
                Button(
                    onClick = { isEditingTime = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar Hora de Atención", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mascota: ${cita.nombreMascota ?: "N/A"}", fontSize = 12.sp, color = Color.Black)
            }

            if (!cita.duenioTelefono.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tel: ${cita.duenioTelefono}", fontSize = 11.sp, color = Color.Gray)
                }
            }

            if (!cita.duenioCorreo.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF7DA581), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Email: ${cita.duenioCorreo}", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            Text("Ficha Médica", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF7DA581))
            Spacer(Modifier.height(8.dp))

            if (!cita.diagnostico.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F1EB),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Diagnóstico", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFF7DA581))
                        Text(cita.diagnostico, fontSize = 12.sp, color = Color.Black)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (!cita.procedimientos.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F1EB),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Procedimientos", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFF7DA581))
                        Text(cita.procedimientos, fontSize = 12.sp, color = Color.Black)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (!cita.recomendaciones.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F1EB),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Recomendaciones", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFF7DA581))
                        Text(cita.recomendaciones, fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}

