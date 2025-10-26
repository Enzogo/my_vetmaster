package com.proyect.myvet.mascotas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.network.MascotaDto
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun GestionMascotasScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mascotas by remember { mutableStateOf<List<MascotaDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Load mascotas from backend on start
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
            mascotas = api.getMyMascotas()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gestión de Mascotas", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (isLoading) {
            Text("Cargando...")
            return@Column
        }

        if (mascotas.isEmpty()) {
            Text("No tienes mascotas registradas.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mascotas, key = { it.id ?: System.currentTimeMillis() }) { m ->
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
                                scope.launch {
                                    try {
                                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                                        api.updateMascota(
                                            m.id!!,
                                            com.proyect.myvet.network.MascotaUpdateRequest(
                                                nombre = nombre,
                                                especie = especie,
                                                raza = raza.ifBlank { null },
                                                fechaNacimiento = null,
                                                sexo = null
                                            )
                                        )
                                        // Reload list
                                        mascotas = api.getMyMascotas()
                                        Toast.makeText(context, "Mascota actualizada", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        val code = (e as? HttpException)?.code()
                                        if (code == 401) {
                                            Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }) { Text("Guardar cambios") }

                            Button(onClick = {
                                scope.launch {
                                    try {
                                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                                        api.deleteMascota(m.id!!)
                                        // Reload list
                                        mascotas = api.getMyMascotas()
                                        Toast.makeText(context, "Mascota eliminada", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        val code = (e as? HttpException)?.code()
                                        if (code == 401) {
                                            Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}