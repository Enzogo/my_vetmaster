package com.proyect.myvet.historial

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HistorialScreen(navController: NavController) {
    val context = LocalContext.current
    var citas by remember { mutableStateOf<List<HistorialCita>>(emptyList()) }

    // Este LaunchedEffect se volverá a ejecutar cada vez que vuelvas a esta pantalla.
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        citas = HistorialManager.obtenerCitas(context)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Historial de Citas", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        if (citas.isEmpty()) {
            Text("No hay citas en el historial.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(citas) { cita ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val citaJson = Gson().toJson(cita)
                                val encodedJson = URLEncoder.encode(citaJson, StandardCharsets.UTF_8.toString())
                                navController.navigate("detalle_historial/$encodedJson")
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mascota: ${cita.mascota}", fontWeight = FontWeight.Bold)
                            Text("Fecha: ${cita.fecha} - ${cita.hora}")
                        }
                    }
                }
            }
        }
    }
}