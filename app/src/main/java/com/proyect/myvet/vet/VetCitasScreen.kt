package com.proyect.myvet.vet

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.network.VetApi
import com.proyect.myvet.network.VetProfileResponse
import retrofit2.HttpException

@Composable
fun VetPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val authVM = LocalAuthViewModel.current
    var loading by remember { mutableStateOf(true) }
    var data by remember { mutableStateOf<VetProfileResponse?>(null) }

    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.authed(context).create(VetApi::class.java)
            data = api.me()
        } catch (e: Exception) {
            val code = (e as? HttpException)?.code()
            val msg = when (code) {
                401 -> "Sesión expirada. Inicia sesión."
                403 -> "Solo veterinarios."
                404 -> "Falta GET /api/vet/me en backend."
                else -> "Error al cargar perfil"
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi perfil (Veterinario)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            val p = data
            if (p == null) {
                Text("No se pudieron cargar tus datos.")
            } else {
                // Datos personales
                InfoRow("Nombre", p.nombre)
                InfoRow("Email", p.email)
                InfoRow("Teléfono", p.telefono)
                InfoRow("Dirección", p.direccion)
                Spacer(Modifier.height(12.dp))
                Text("Clínica", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                InfoRow("Nombre clínica", p.clinicName)
                InfoRow("Teléfono clínica", p.clinicPhone)
                InfoRow("Dirección clínica", p.clinicAddress)
                InfoRow("Especialidad", p.speciality)
                InfoRow("Nº de registro", p.registrationNumber)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate("editar_perfil") }, modifier = Modifier.weight(1f)) {
                    Text("Editar perfil")
                }
                Button(onClick = { authVM.logout() }, modifier = Modifier.weight(1f)) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}