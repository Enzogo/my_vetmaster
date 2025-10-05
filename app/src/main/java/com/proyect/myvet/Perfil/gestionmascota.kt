package com.proyect.myvet.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.mascotas.Mascota
import com.proyect.myvet.mascotas.MascotaManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionMascotasScreen(navController: NavController) {
    val context = LocalContext.current
    var mascotas by remember { mutableStateOf(MascotaManager.obtenerMascotas(context)) }

    // Refresca la lista cuando vuelves a esta pantalla
    LaunchedEffect(navController.currentBackStackEntry) {
        mascotas = MascotaManager.obtenerMascotas(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Mascotas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mascotas) { mascota ->
                MascotaCard(
                    mascota = mascota,
                    onEdit = {
                        // Navega a la pantalla de registro en modo edición
                        navController.navigate("registrar_mascota?mascotaId=${mascota.id}")
                    },
                    onDelete = {
                        MascotaManager.eliminarMascota(context, mascota.id)
                        // Actualiza la UI al instante
                        mascotas = mascotas.filter { it.id != mascota.id }
                    }
                )
            }
        }
    }
}

@Composable
private fun MascotaCard(mascota: Mascota, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Mascota") },
            text = { Text("¿Seguro que quieres eliminar a ${mascota.nombre}? Esta acción es permanente.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(mascota.nombre, fontWeight = FontWeight.Bold)
                Text("${mascota.especie} - ${mascota.raza}")
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Editar")
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}