package com.proyect.myvet.citas

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun CitasScreen(
    navController: NavController,
    motivoInicial: String? = null // reintroducido para prellenar el motivo desde prediagnóstico
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Decodificar el motivo inicial (si vino por query param)
    val motivoPrefill = remember(motivoInicial) {
        motivoInicial?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) } ?: ""
    }

    // Estado UI
    var motivoCita by remember { mutableStateOf(motivoPrefill) }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }

    // Mascotas desde backend (IDs reales de Mongo)
    var mascotas by remember { mutableStateOf<List<MascotaDto>>(emptyList()) }
    var mascotaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var mascotasLoading by remember { mutableStateOf(false) }

    // Cargar mascotas al abrir
    LaunchedEffect(Unit) {
        mascotasLoading = true
        try {
            val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
            mascotas = api.getMyMascotas()
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudieron cargar mascotas", Toast.LENGTH_SHORT).show()
        } finally {
            mascotasLoading = false
        }
    }

    val calendar = Calendar.getInstance()

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) Toast.makeText(context, "Permiso notificaciones denegado", Toast.LENGTH_SHORT).show()
    }

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrar/Reprogramar Cita", fontSize = 22.sp)

        Spacer(Modifier.height(12.dp))

        // Selector de mascota (usa IDs reales)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = mascotas.firstOrNull { it.id == mascotaSeleccionadaId }?.nombre ?: if (mascotasLoading) "Cargando mascotas..." else "Seleccionar Mascota",
                onValueChange = {},
                label = { Text("Mascota") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = motivoCita,
            onValueChange = { motivoCita = it },
            label = { Text("Motivo de Cita") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { datePickerDialog.show() }) {
                Text(if (selectedDateText.isEmpty()) "Seleccionar Fecha" else selectedDateText)
            }
            Button(onClick = { timePickerDialog.show() }) {
                Text(if (selectedTimeText.isEmpty()) "Seleccionar Hora" else selectedTimeText)
            }
        }

        Spacer(Modifier.height(24.dp))

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

                        // Programar recordatorio local
                        scheduleExactAlarm(context, calendar.timeInMillis, created.motivo ?: motivoCita)

                        // Historial local opcional
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

                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Cita guardada", Toast.LENGTH_SHORT).show()
                            // Permanecer en la app: ir a la pestaña Citas
                            navController.navigate(NavigationItem.Citas.route)
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        launch(Dispatchers.Main) {
                            if (code == 401) {
                                Toast.makeText(context, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al guardar cita", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) { Text("Guardar cita") }
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
        // No romper UX si falla programar alarma
    }
}