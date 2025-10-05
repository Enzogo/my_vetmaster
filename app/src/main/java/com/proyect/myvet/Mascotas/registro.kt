package com.proyect.myvet.mascotas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMascotaScreen(navController: NavController, mascotaId: Long? = null) {
    val context = LocalContext.current
    val isEditMode = mascotaId != null

    // Carga la mascota si estamos en modo edición
    val mascotaAEditar = remember(mascotaId) {
        if (isEditMode) MascotaManager.obtenerMascotaPorId(context, mascotaId!!) else null
    }

    var nombre by remember { mutableStateOf(mascotaAEditar?.nombre ?: "") }
    var especie by remember { mutableStateOf(mascotaAEditar?.especie ?: "") }
    var raza by remember { mutableStateOf(mascotaAEditar?.raza ?: "") }
    var fechaNacimiento by remember { mutableStateOf(mascotaAEditar?.fechaNacimiento ?: "") }
    var sexo by remember { mutableStateOf(mascotaAEditar?.sexo ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Mascota" else "Registrar Mascota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = especie, onValueChange = { especie = it }, label = { Text("Especie (Ej: Perro, Gato)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de Nacimiento (DD/MM/AAAA)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = sexo, onValueChange = { sexo = it }, label = { Text("Sexo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && especie.isNotBlank()) {
                        if (isEditMode) {
                            val mascotaActualizada = Mascota(mascotaId!!, nombre, especie, raza, fechaNacimiento, sexo)
                            MascotaManager.actualizarMascota(context, mascotaActualizada)
                            Toast.makeText(context, "${nombre} actualizado", Toast.LENGTH_SHORT).show()
                        } else {
                            val nuevaMascota = Mascota(nombre = nombre, especie = especie, raza = raza, fechaNacimiento = fechaNacimiento, sexo = sexo)
                            MascotaManager.guardarMascota(context, nuevaMascota)
                            Toast.makeText(context, "${nombre} guardado", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Nombre y especie son obligatorios", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (isEditMode) "Actualizar Mascota" else "Guardar Mascota")
            }
        }
    }
}