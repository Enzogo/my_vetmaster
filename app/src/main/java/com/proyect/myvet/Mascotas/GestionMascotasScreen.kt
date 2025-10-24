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

@Composable
fun GestionMascotasScreen(navController: NavController) {
    val context = LocalContext.current
    var mascotas by remember { mutableStateOf(MascotaManager.obtenerMascotas(context)) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gestión de Mascotas", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (mascotas.isEmpty()) {
            Text("No tienes mascotas registradas.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mascotas, key = { it.id }) { m ->
                var nombre by remember(m.id) { mutableStateOf(m.nombre) }
                var especie by remember(m.id) { mutableStateOf(m.especie) }
                var raza by remember(m.id) { mutableStateOf(m.raza ?: "") }
                var fecha by remember(m.id) { mutableStateOf(m.fechaNacimiento ?: "") }
                var sexo by remember(m.id) { mutableStateOf(m.sexo ?: "") }

                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("ID: ${m.id}", style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = especie, onValueChange = { especie = it }, label = { Text("Especie") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = raza, onValueChange = { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha Nacimiento") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = sexo, onValueChange = { sexo = it }, label = { Text("Sexo") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = {
                                if (nombre.isBlank() || especie.isBlank()) {
                                    Toast.makeText(context, "Nombre y especie son obligatorios", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val actualizado = Mascota(
                                    id = m.id,
                                    nombre = nombre,
                                    especie = especie,
                                    raza = raza.ifBlank { null },
                                    fechaNacimiento = fecha.ifBlank { null },
                                    sexo = sexo.ifBlank { null }
                                )
                                MascotaManager.actualizarMascota(context, actualizado)
                                mascotas = MascotaManager.obtenerMascotas(context)
                                Toast.makeText(context, "Mascota actualizada", Toast.LENGTH_SHORT).show()
                            }) { Text("Guardar cambios") }

                            Button(onClick = {
                                MascotaManager.eliminarMascota(context, m.id)
                                mascotas = MascotaManager.obtenerMascotas(context)
                                Toast.makeText(context, "Mascota eliminada", Toast.LENGTH_SHORT).show()
                            }) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}