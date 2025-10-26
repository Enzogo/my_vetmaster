package com.proyect.myvet.mascotas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyect.myvet.auth.AuthViewModel
import com.proyect.myvet.owner.OwnerViewModel

@Composable
fun GestionMascotasScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: OwnerViewModel = viewModel()
    val authVM: AuthViewModel = viewModel()
    val state by vm.state.collectAsState()

    // Cargar mascotas al abrir la pantalla
    LaunchedEffect(Unit) {
        vm.refreshLists()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gestión de Mascotas", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (state.loading) {
            CircularProgressIndicator()
            return@Column
        }

        if (state.mascotas.isEmpty()) {
            Text("No tienes mascotas registradas.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.mascotas, key = { it.id ?: System.currentTimeMillis() }) { m ->
                var nombre by remember(m.id) { mutableStateOf(m.nombre ?: "") }
                var especie by remember(m.id) { mutableStateOf(m.especie ?: "") }
                var raza by remember(m.id) { mutableStateOf(m.raza ?: "") }

                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("ID: ${m.id}", style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = especie, onValueChange = { especie = it }, label = { Text("Especie") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = {
                                if (nombre.isBlank() || especie.isBlank()) {
                                    Toast.makeText(context, "Nombre y especie son obligatorios", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                vm.updateMascota(
                                    m.id ?: "",
                                    nombre,
                                    especie,
                                    raza.ifBlank { null },
                                    null,
                                    null,
                                    onDone = { Toast.makeText(context, "Mascota actualizada", Toast.LENGTH_SHORT).show() },
                                    onError = { err ->
                                        val code = (err as? retrofit2.HttpException)?.code()
                                        if (code == 401) {
                                            Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
                                            authVM.logout()
                                        } else {
                                            Toast.makeText(context, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }) { Text("Guardar cambios") }

                            Button(onClick = {
                                vm.deleteMascota(
                                    m.id ?: "",
                                    onDone = { Toast.makeText(context, "Mascota eliminada", Toast.LENGTH_SHORT).show() },
                                    onError = { err ->
                                        val code = (err as? retrofit2.HttpException)?.code()
                                        if (code == 401) {
                                            Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
                                            authVM.logout()
                                        } else {
                                            Toast.makeText(context, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}
