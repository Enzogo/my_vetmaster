package com.proyect.myvet.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegistrarCitasScreen(navController: NavController) {
    val vm: OwnerViewModel = viewModel()
    val state by vm.state.collectAsState()

    // Cargar mascotas al entrar
    LaunchedEffect(Unit) { vm.refreshLists() }

    var motivo by remember { mutableStateOf("") }
    var fechaIso by remember { mutableStateOf("") } // Puedes reemplazarlo por pickers si quieres
    var selectedIndex by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registrar Cita", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // Selector simple de mascota
        if (state.mascotas.isEmpty()) {
            Text("No tienes mascotas registradas. Registra una primero.")
        } else {
            Text("Mascota")
            Spacer(Modifier.height(4.dp))
            var expanded by remember { mutableStateOf(false) }
            val selectedName = state.mascotas.getOrNull(selectedIndex)?.nombre ?: ""
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedName.isNullOrBlank()) "Selecciona una mascota" else selectedName!!)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                state.mascotas.forEachIndexed { index, m ->
                    DropdownMenuItem(
                        text = { Text(m.nombre ?: "(sin nombre)") },
                        onClick = {
                            selectedIndex = index
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it },
            label = { Text("Motivo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = fechaIso,
            onValueChange = { fechaIso = it },
            label = { Text("Fecha y hora (ISO o texto)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val mascotaId = state.mascotas.getOrNull(selectedIndex)?.id
                if (mascotaId.isNullOrBlank()) {
                    error = "Selecciona una mascota válida"
                    return@Button
                }
                vm.addCita(
                    fechaIso = fechaIso,
                    motivo = motivo,
                    mascotaId = mascotaId,
                    onDone = {
                        // Vuelve atrás; si quieres, navega a una pestaña dentro de MainScreen
                        navController.popBackStack()
                    },
                    onError = { error = it }
                )
            },
            enabled = !state.loading && motivo.isNotBlank() && fechaIso.isNotBlank() && state.mascotas.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(if (state.loading) "Guardando..." else "Guardar")
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}