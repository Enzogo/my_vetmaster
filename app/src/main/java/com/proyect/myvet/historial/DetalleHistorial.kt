package com.proyect.myvet.historial

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleHistorialScreen(navController: NavController, cita: HistorialCita) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta cita del historial? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        HistorialManager.eliminarCita(context, cita.id)
                        Toast.makeText(context, "Cita eliminada", Toast.LENGTH_SHORT).show()
                        showDialog = false
                        navController.popBackStack() // Vuelve a la pantalla de historial
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // --- ¡NUEVO BOTÓN DE ELIMINAR! ---
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Cita", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Detalles de la Cita", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Divider()
            InfoRow("Mascota:", cita.mascota)
            InfoRow("Dueño:", cita.dueno)
            InfoRow("Fecha:", cita.fecha)
            InfoRow("Hora:", cita.hora)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Motivo de la Cita:", fontWeight = FontWeight.Bold)
            Text(cita.motivo)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        Text(value)
    }
}