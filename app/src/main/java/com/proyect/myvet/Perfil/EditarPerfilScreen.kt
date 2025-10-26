package com.proyect.myvet.perfil

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.network.VetApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun EditarPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val authVM = LocalAuthViewModel.current
    val authState by authVM.state.collectAsState()
    val isVet = authState.role == "veterinario"

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var clinicName by remember { mutableStateOf("") }
    var clinicPhone by remember { mutableStateOf("") }
    var clinicAddress by remember { mutableStateOf("") }
    var speciality by remember { mutableStateOf("") }
    var registrationNumber by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(if (isVet) "Editar perfil (Veterinario)" else "Editar perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        if (isVet) {
            Spacer(Modifier.height(12.dp))
            Text("Datos de clínica", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(clinicName, { clinicName = it }, label = { Text("Nombre de la clínica") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(clinicPhone, { clinicPhone = it }, label = { Text("Teléfono de la clínica") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(clinicAddress, { clinicAddress = it }, label = { Text("Dirección de la clínica") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(speciality, { speciality = it }, label = { Text("Especialidad") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(registrationNumber, { registrationNumber = it }, label = { Text("Nº de registro") }, modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (isVet) {
                            val api = RetrofitClient.authed(context).create(VetApi::class.java)
                            val ok = api.saveProfile(
                                com.proyect.myvet.network.VetProfileRequest(
                                    nombre = nombre.ifBlank { null },
                                    telefono = telefono.ifBlank { null },
                                    direccion = direccion.ifBlank { null },
                                    clinicName = clinicName.ifBlank { null },
                                    clinicPhone = clinicPhone.ifBlank { null },
                                    clinicAddress = clinicAddress.ifBlank { null },
                                    speciality = speciality.ifBlank { null },
                                    registrationNumber = registrationNumber.ifBlank { null }
                                )
                            )
                            if (ok) launch(Dispatchers.Main) { Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show() }
                        } else {
                            val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                            val ok = api.saveProfile(OwnerApi.OwnerProfileRequest(nombre, telefono, direccion))
                            if (ok) launch(Dispatchers.Main) { Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show() }
                        }
                    } catch (e: Exception) {
                        val code = (e as? HttpException)?.code()
                        launch(Dispatchers.Main) {
                            val msg = when (code) {
                                401 -> "Sesión expirada. Inicia sesión."
                                404 -> if (isVet) "Falta /api/vet/me/profile en backend." else "Endpoint de perfil no encontrado."
                                else -> "Error al guardar perfil"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }
    }
}