package com.proyect.myvet.mascotas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.proyect.myvet.auth.AuthViewModel
import com.proyect.myvet.network.MascotaCreateRequest
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun RegistrarMascotaScreen(navController: NavController, mascotaId: Long? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authVM: AuthViewModel = viewModel()

    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(especie, { especie = it }, label = { Text("Especie") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(raza, { raza = it }, label = { Text("Raza") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                        val res = api.createMascota(MascotaCreateRequest(nombre, especie, raza.ifBlank { null }, null, null))
                        Toast.makeText(context, "Mascota guardada: ${res.nombre}", Toast.LENGTH_SHORT).show()
                        navController.navigate("gestion_mascotas")
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        if (code == 401) {
                            Toast.makeText(context, "Sesión expirada. Inicia sesión.", Toast.LENGTH_SHORT).show()
                            authVM.logout() // MainActivity redirige al login
                        } else {
                            Toast.makeText(context, "Error al guardar mascota", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }
    }
}