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
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    navController: NavController,
    motivoInicial: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mascotas by remember { mutableStateOf<List<com.proyect.myvet.network.MascotaDto>>(emptyList()) }
    var mascotaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var mascotasLoading by remember { mutableStateOf(false) }

    // Prellenar el motivo si viene desde prediagnóstico (decodificar URL)
    val motivoPrefill = remember(motivoInicial) {
        motivoInicial?.let {
            try {
                java.net.URLDecoder.decode(it, "UTF-8")
            } catch (e: Exception) {
                it
            }
        } ?: ""
    }

    var motivoCita by remember { mutableStateOf(motivoPrefill) }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }
    var calendar = remember { Calendar.getInstance() }

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

    // Cargar mascotas del usuario al iniciar
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
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Agendar Cita", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Selecciona mascota, fecha y hora", style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.7f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selector de mascota
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Selecciona tu mascota", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF7DA581))
                Spacer(Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
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
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        if (mascotas.isEmpty()) {
                            DropdownMenuItem(text = { Text("No hay mascotas registradas") }, onClick = { expanded = false })
                        } else {
                            mascotas.forEach { m ->
                                DropdownMenuItem(text = { Text(m.nombre ?: "(sin nombre)", color = Color.Black) }, onClick = {
                                    mascotaSeleccionadaId = m.id
                                    expanded = false
                                })
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Motivo
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Motivo de la cita", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF7DA581))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = motivoCita,
                    onValueChange = { motivoCita = it },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    colors = textFieldColors,
                    minLines = 3,
                    maxLines = 5
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Fecha y hora
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Fecha y hora", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF7DA581))
                Spacer(Modifier.height(12.dp))

                val today = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        selectedDateText = sdf.format(calendar.time)
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                )

                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        selectedTimeText = tf.format(calendar.time)
                    },
                    today.get(Calendar.HOUR_OF_DAY),
                    today.get(Calendar.MINUTE),
                    true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (selectedDateText.isEmpty()) "Seleccionar fecha" else selectedDateText)
                    }
                    OutlinedButton(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (selectedTimeText.isEmpty()) "Seleccionar hora" else selectedTimeText)
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
                        val response = api.createCita(CitaCreateRequest(fechaIso, motivoCita, mascotaId))

                        if (response.isSuccessful && response.body() != null) {
                            val created = response.body()!!

                            // Sólo programar recordatorio después de confirmar persistencia
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

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "✓ Cita agendada exitosamente", Toast.LENGTH_SHORT).show()
                                navController.navigate(NavigationItem.Citas.route)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error al guardar cita: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        withContext(Dispatchers.Main) {
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

        Spacer(Modifier.height(24.dp))

        // Aquí podrías listar próximas citas, historial, etc. (no modificado)
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