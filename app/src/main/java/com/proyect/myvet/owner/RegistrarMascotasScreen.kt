package com.proyect.myvet.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegistrarMascotaScreen(navController: NavController) {
    val vm: OwnerViewModel = viewModel()
    val state by vm.state.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registrar Mascota", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = especie, onValueChange = { especie = it }, label = { Text("Especie") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza (opcional)") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                vm.addMascota(
                    nombre, especie, raza.ifBlank { null },
                    onDone = { navController.popBackStack() },
                    onError = { /* Muestra snackbar si quieres */ }
                )
            },
            enabled = !state.loading && nombre.isNotBlank() && especie.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text(if (state.loading) "Guardando..." else "Guardar") }
    }
}