package com.proyect.myvet.perfil

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proyect.myvet.NavigationItem
import com.proyect.myvet.historial.HistorialCita
import com.proyect.myvet.historial.HistorialManager
import com.proyect.myvet.mascotas.Mascota
import com.proyect.myvet.mascotas.MascotaManager

@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current

    val mascotas by remember { mutableStateOf(MascotaManager.obtenerMascotas(context)) }
    val citas by remember { mutableStateOf(HistorialManager.obtenerCitas(context)) }

    val email = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("email", null) }
    val nombre = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("nombre", null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil de Dueño", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Nombre: ${nombre ?: "-"}")
        Text("Correo: ${email ?: "-"}")

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.navigate("editar_perfil") }, modifier = Modifier.weight(1f).height(48.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Rellenar Información")
            }
            Button(onClick = { navController.navigate(NavigationItem.Citas.route) }, modifier = Modifier.weight(1f).height(48.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar Cita")
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = { navController.navigate("registrar_mascota") }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Registrar Mascota")
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = { navController.navigate("gestion_mascotas") }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Icon(Icons.Filled.Pets, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Gestionar Mis Mascotas")
        }

        Spacer(Modifier.height(16.dp))

        Text("Mis Mascotas", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        SimpleMascotaList(mascotas)

        Spacer(Modifier.height(8.dp))

        Text("Mis Citas", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        SimpleCitasList(citas)

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                navController.navigate("auth_screen") { popUpTo(0) { inclusive = true } }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun SimpleMascotaList(mascotas: List<Mascota>) {
    LazyColumn(Modifier.fillMaxWidth().heightIn(min = 0.dp, max = 220.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(mascotas) { m ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(m.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("Especie: ${m.especie}  Raza: ${m.raza ?: "-"}")
                }
            }
        }
    }
}

@Composable
private fun SimpleCitasList(citas: List<HistorialCita>) {
    LazyColumn(Modifier.fillMaxWidth().heightIn(min = 0.dp, max = 220.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(citas) { c ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Fecha: ${c.fecha}", style = MaterialTheme.typography.titleMedium)
                    Text("Hora: ${c.hora}  Motivo: ${c.motivo}")
                    Text("Mascota(s): ${c.mascota} | Dueño: ${c.dueno}")
                }
            }
        }
    }
}