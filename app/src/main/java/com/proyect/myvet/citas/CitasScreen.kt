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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.proyect.myvet.NavigationItem
import com.proyect.myvet.Notificacion
import com.proyect.myvet.historial.HistorialCita
import com.proyect.myvet.historial.HistorialManager
import com.proyect.myvet.mascotas.Mascota
import com.proyect.myvet.mascotas.MascotaManager
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun CitasScreen(navController: NavController, motivoInicial: String? = null) {
    var nombreMascota by remember { mutableStateOf("") }
    var nombreDueno by remember { mutableStateOf("") }
    val motivoDecodificado = remember(motivoInicial) { motivoInicial?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) } ?: "" }
    var motivoCita by remember { mutableStateOf(motivoDecodificado) }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // --- LÓGICA PARA SELECCIÓN MÚLTIPLE ---
    val mascotasRegistradas = remember { MascotaManager.obtenerMascotas(context) }
    var mascotasSeleccionadas by remember { mutableStateOf<List<Mascota>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    // Actualiza el campo de texto cuando cambia la selección
    LaunchedEffect(mascotasSeleccionadas) {
        nombreMascota = mascotasSeleccionadas.joinToString(", ") { it.nombre }
    }

    if (showDialog) {
        MultiSelectMascotasDialog(
            mascotas = mascotasRegistradas,
            seleccionadas = mascotasSeleccionadas,
            onDismiss = { showDialog = false },
            onConfirm = {
                mascotasSeleccionadas = it
                showDialog = false
            }
        )
    }

    // ... (Pickers de fecha/hora y permisos no cambian)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { if (!it) Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show() }
    val datePickerDialog = DatePickerDialog(context, { _, y, m, d -> selectedDateText = "$d/${m+1}/$y"; calendar.set(y,m,d) }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    val timePickerDialog = TimePickerDialog(context, { _, h, m -> selectedTimeText = String.format("%02d:%02d", h, m); calendar.set(Calendar.HOUR_OF_DAY, h); calendar.set(Calendar.MINUTE, m) }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Registrar Nueva Cita", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        if (mascotasRegistradas.isNotEmpty()) {
            Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (mascotasSeleccionadas.isEmpty()) "Seleccionar Mascotas" else "Mascotas seleccionadas")
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(value = nombreMascota, onValueChange = { nombreMascota = it }, label = { Text("Nombre(s) de Mascota(s)") }, modifier = Modifier.fillMaxWidth(), enabled = mascotasSeleccionadas.isEmpty())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = nombreDueno, onValueChange = { nombreDueno = it }, label = { Text("Nombre de Dueño") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = motivoCita, onValueChange = { motivoCita = it }, label = { Text("Motivo de Cita") }, modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { datePickerDialog.show() }) { Text(if (selectedDateText.isEmpty()) "Seleccionar Fecha" else selectedDateText) }
            Button(onClick = { timePickerDialog.show() }) { Text(if (selectedTimeText.isEmpty()) "Seleccionar Hora" else selectedTimeText) }
        }
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (selectedDateText.isEmpty() || selectedTimeText.isEmpty() || nombreMascota.isBlank()) {
                    Toast.makeText(context, "Mascota(s), fecha y hora son obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val nuevaCita = HistorialCita(System.currentTimeMillis(), nombreMascota, nombreDueno, motivoCita, selectedDateText, selectedTimeText)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        scheduleExactAlarmAndSave(context, calendar.timeInMillis, nuevaCita, navController)
                    } else {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    scheduleExactAlarmAndSave(context, calendar.timeInMillis, nuevaCita, navController)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Guardar y Programar Recordatorio", fontSize = 18.sp)
        }
    }
}

// Diálogo para selección múltiple
@Composable
fun MultiSelectMascotasDialog(mascotas: List<Mascota>, seleccionadas: List<Mascota>, onDismiss: () -> Unit, onConfirm: (List<Mascota>) -> Unit) {
    val tempSelection = remember { mutableStateListOf<Mascota>().apply { addAll(seleccionadas) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Mascotas") },
        text = {
            LazyColumn {
                items(mascotas) { mascota ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (tempSelection.contains(mascota)) tempSelection.remove(mascota) else tempSelection.add(mascota)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempSelection.contains(mascota),
                            onCheckedChange = { isChecked ->
                                if (isChecked) tempSelection.add(mascota) else tempSelection.remove(mascota)
                            }
                        )
                        Text(mascota.nombre, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(tempSelection.toList()) }) { Text("Confirmar") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancelar") } }
    )
}



@SuppressLint("ScheduleExactAlarm")
private fun scheduleExactAlarmAndSave(
    context: Context,
    timeInMillis: Long,
    cita: HistorialCita,
    navController: NavController
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        // IMPORTANTE: usa NEW_TASK cuando lanzas Settings desde un contexto que puede no ser una Activity
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        // Aún así guardamos local y continuamos navegación
    }

    val intent = Intent(context, Notificacion::class.java).apply {
        putExtra("MASCOTA_NOMBRE", cita.mascota)
        putExtra("MOTIVO_CITA", cita.motivo)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        cita.id.toInt(), // si prefieres, usa (cita.id and Int.MAX_VALUE.toLong()).toInt()
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    } catch (_: Exception) {
        // Si algo falla, aún así seguimos guardando y navegando
    }

    // Guarda local (como ya usas en tu app)
    HistorialManager.guardarCita(context, cita)
    Toast.makeText(context, "Cita Guardada Exitosamente", Toast.LENGTH_SHORT).show()

    // Guardado remoto no bloqueante (si el backend todavía no tiene el endpoint, no rompe el flujo)
    try {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val api = com.proyect.myvet.network.RetrofitClient
                .authed(context)
                .create(com.proyect.myvet.network.OwnerApi::class.java)

            // Combina fecha + hora (ajústalo si tu backend requiere ISO real)
            val fechaIso = "${cita.fecha} ${cita.hora}"
            // De momento, usamos el nombre como "mascotaId"; cuando tu backend devuelva IDs reales, cámbialo
            api.createCita(com.proyect.myvet.network.CitaCreateRequest(fechaIso, cita.motivo, cita.mascota))
        }
    } catch (_: Exception) {
        // Ignoramos errores remotos para no interrumpir UX
    }

    // Ir a la pestaña Historial dentro de MainScreen
    navController.navigate(com.proyect.myvet.NavigationItem.Historial.route)
}