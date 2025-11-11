package com.proyect.myvet.perfil

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
    var loading by remember { mutableStateOf(false) }

    fun load() = scope.launch {
        loading = true
        try {
            val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
            mascotas = api.getMyMascotas()
        } catch (_: Exception) {
            Toast.makeText(context, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
        } finally { loading = false }
    }

    LaunchedEffect(Unit) { load() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { navController.navigate("registrar_mascota") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar nueva mascota")
        }
        Spacer(Modifier.height(12.dp))
        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        LazyColumn {
            items(mascotas) { m ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(m.nombre ?: "(sin nombre)", style = MaterialTheme.typography.titleMedium)
                        Text("${m.especie ?: ""} ${m.raza ?: ""}".trim())
                        if (m.fechaNacimiento != null || m.sexo != null) {
                            val detalles = listOfNotNull(
                                m.fechaNacimiento?.let { "Edad: $it" },
                                m.sexo?.let { "Sexo: $it" }
                            ).joinToString(" • ")
                            Text(detalles, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    try {
                                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                                        api.deleteMascota(m.id ?: return@launch)
                                        Toast.makeText(context, "Mascota eliminada", Toast.LENGTH_SHORT).show()
                                        load()
                                    } catch (e: Exception) {
                                        val code = (e as? HttpException)?.code()
                                        if (code == 401) Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
                                        else Toast.makeText(context, "Error al eliminar mascota", Toast.LENGTH_SHORT).show()
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