package com.proyect.myvet.perfil

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun EditarPerfilDuenoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember {
        mutableStateOf(context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("nombre", "") ?: "")
    }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Rellenar Información del Dueño", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    try {
                        val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                        val ok = api.saveProfile(OwnerApi.OwnerProfileRequest(nombre = nombre, telefono = telefono, direccion = direccion))
                        if (!ok) throw IllegalStateException("El backend respondió false")
                        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().putString("nombre", nombre).apply()
                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } catch (e: HttpException) {
                        error = "HTTP ${e.code()}"
                    } catch (e: Exception) {
                        error = e.message
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading && nombre.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text(if (loading) "Guardando..." else "Guardar") }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}