package com.proyect.myvet.citas

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
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
import com.proyect.myvet.Notificacion
import com.proyect.myvet.historial.HistorialCita
import com.proyect.myvet.historial.HistorialManager
import com.proyect.myvet.network.CitaCreateRequest
import com.proyect.myvet.network.MascotaDto
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun CitasScreen(
    navController: NavController,
    motivoInicial: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val motivoPrefill = remember(motivoInicial) {
        motivoInicial?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) } ?: ""
    }

    var motivoCita by remember { mutableStateOf(motivoPrefill) }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }

    var mascotas by remember { mutableStateOf<List<MascotaDto>>(emptyList()) }
    var mascotaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var mascotasLoading by remember { mutableStateOf(false) }

    // Colores para campos de texto (texto negro)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedBorderColor = Color(0xFF7DA581),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFF7DA581),
        unfocusedLabelColor = Color.Gray
    )

    // Cargar mascotas
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

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            selectedDateText = "%02d/%02d/%04d".format(d, m + 1, y)
            calendar.set(y, m, d)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, m ->
            selectedTimeText = String.format("%02d:%02d", h, m)
            calendar.set(Calendar.HOUR_OF_DAY, h)
            calendar.set(Calendar.MINUTE, m)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

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
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Agendar Cita",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "Programa tu consulta veterinaria",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selección de mascota
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Mascota",
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

        // Motivo de la cita
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Motivo de la Cita",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7DA581)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = motivoCita,
                    onValueChange = { motivoCita = it },
                    label = { Text("Describe el motivo") },
                    placeholder = { Text("Ej: Consulta general, vacunación, control...") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    minLines = 3,
                    maxLines = 5
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Fecha y hora
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Fecha y Hora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7DA581)
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7DA581)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (selectedDateText.isEmpty()) "Fecha" else selectedDateText,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedButton(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7DA581)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (selectedTimeText.isEmpty()) "Hora" else selectedTimeText,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón de agendar
        Button(
            onClick = {
                val mascotaId = mascotaSeleccionadaId
                if (mascotaId.isNullOrBlank() || selectedDateText.isEmpty() || selectedTimeText.isEmpty()) {
                    Toast.makeText(context, "Mascota, fecha y hora son obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val fechaIso = "$selectedDateText $selectedTimeText"

                scope.launch(Dispatchers.IO) {
                    try {
                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                        val created = api.createCita(CitaCreateRequest(fechaIso, motivoCita, mascotaId))

                        // Recordatorio local
                        scheduleExactAlarm(context, calendar.timeInMillis, created.motivo ?: motivoCita)

                        // Historial local (si existe en tu app)
                        try {
                            HistorialManager.guardarCita(
                                context,
                                HistorialCita(
                                    System.currentTimeMillis(),
                                    "(id ${created.mascotaId ?: mascotaId})",
                                    "",
                                    created.motivo ?: motivoCita,
                                    selectedDateText,
                                    selectedTimeText
                                )
                            )
                        } catch (_: Throwable) { /* ignora si no existe */ }

                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "✓ Cita agendada exitosamente", Toast.LENGTH_SHORT).show()
                            navController.navigate(NavigationItem.Citas.route)
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        launch(Dispatchers.Main) {
                            if (code == 401) {
                                Toast.makeText(context, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al guardar cita: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agendar Cita", fontWeight = FontWeight.Bold)
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
private fun scheduleExactAlarm(
    context: Context,
    timeInMillis: Long,
    motivo: String
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        context.startActivity(intent)
    }
    val intent = Intent(context, Notificacion::class.java).apply {
        putExtra("MOTIVO_CITA", motivo)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        (System.currentTimeMillis() and Int.MAX_VALUE.toLong()).toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    try {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    } catch (_: Exception) {
        // no rompas UX si falla
    }
}