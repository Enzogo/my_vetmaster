package com.proyect.myvet.perfil

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun EditarPerfilDuenoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authVM: AuthViewModel = viewModel()

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                        val ok = api.saveProfile(OwnerApi.OwnerProfileRequest(nombre, telefono, direccion))
                        if (ok) {
                            Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show()
                            // Permanecer en la pestaña Perfil
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        if (code == 401) {
                            Toast.makeText(context, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                            authVM.logout() // MainActivity redirige al login
                        } else {
                            Toast.makeText(context, "Error al guardar perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }
    }
}