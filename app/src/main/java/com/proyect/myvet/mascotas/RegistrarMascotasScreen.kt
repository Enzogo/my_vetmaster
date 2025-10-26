package com.proyect.myvet.mascotas

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.network.MascotaCreateRequest
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun RegistrarMascotaScreen(
    navController: NavController,
    mascotaId: Long? = null // reservado por si luego implementas edición local
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }

    // Si en el futuro quieres precargar datos cuando sea edición, puedes usar mascotaId aquí.
    LaunchedEffect(mascotaId) { /* no-op por ahora */ }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Registrar Mascota")
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = especie,
            onValueChange = { especie = it },
            label = { Text("Especie (Perro, Gato, etc.)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = raza,
            onValueChange = { raza = it },
            label = { Text("Raza (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || especie.isBlank()) {
                    Toast.makeText(context, "Nombre y especie son obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                        val res = api.createMascota(
                            MascotaCreateRequest(
                                nombre = nombre.trim(),
                                especie = especie.trim(),
                                raza = raza.ifBlank { null },
                                fechaNacimiento = null,
                                sexo = null
                            )
                        )
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Mascota guardada: ${res.nombre ?: ""}", Toast.LENGTH_SHORT).show()
                            // Volver a la gestión/listado de mascotas
                            navController.navigate("gestion_mascotas")
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        launch(Dispatchers.Main) {
                            if (code == 401) {
                                Toast.makeText(context, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al guardar mascota", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) { Text("Guardar") }
    }
}