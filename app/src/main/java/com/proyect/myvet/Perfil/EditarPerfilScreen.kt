package com.proyect.myvet.perfil

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.network.VetApi
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun EditarPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val authVM = LocalAuthViewModel.current
    val authState by authVM.state.collectAsState()
    val isVet = authState.role == "veterinario"
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var clinicName by remember { mutableStateOf("") }
    var clinicPhone by remember { mutableStateOf("") }
    var clinicAddress by remember { mutableStateOf("") }
    var speciality by remember { mutableStateOf("") }
    var registrationNumber by remember { mutableStateOf("") }

    // Colores para campos de texto (texto negro)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedBorderColor = Color(0xFF7DA581),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFF7DA581),
        unfocusedLabelColor = Color.Gray
    )

    // Cargar datos del perfil existente
    LaunchedEffect(Unit) {
        loading = true
        try {
            if (isVet) {
                val api = RetrofitClient.authed(context).create(VetApi::class.java)
                val data = api.me()
                nombre = data.nombre ?: ""
                telefono = data.telefono ?: ""
                direccion = data.direccion ?: ""
                clinicName = data.clinicName ?: ""
                clinicPhone = data.clinicPhone ?: ""
                clinicAddress = data.clinicAddress ?: ""
                speciality = data.speciality ?: ""
                registrationNumber = data.registrationNumber ?: ""
            } else {
                val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                val data = api.me()
                nombre = data.nombre ?: ""
                telefono = data.telefono ?: ""
                direccion = data.direccion ?: ""
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EB))
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA581)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    if (isVet) Icons.Default.MedicalServices else Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    if (isVet) "Editar Perfil Veterinario" else "Editar Perfil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF7DA581))
            }
        } else {
            // Información Personal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Información Personal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7DA581)
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                }
            }

            // Información de Clínica (solo para veterinarios)
            if (isVet) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Información de la Clínica",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7DA581)
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = clinicName,
                            onValueChange = { clinicName = it },
                            label = { Text("Nombre de la clínica") },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = clinicPhone,
                            onValueChange = { clinicPhone = it },
                            label = { Text("Teléfono de la clínica") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = clinicAddress,
                            onValueChange = { clinicAddress = it },
                            label = { Text("Dirección de la clínica") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = speciality,
                            onValueChange = { speciality = it },
                            label = { Text("Especialidad") },
                            leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = registrationNumber,
                            onValueChange = { registrationNumber = it },
                            label = { Text("Número de registro profesional") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                    }
                }
            }

            // Botones de acción
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
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
                                    if (ok) {
                                        Toast.makeText(context, "✓ Perfil guardado exitosamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                } else {
                                    val api = RetrofitClient.authed(context).create(OwnerApi::class.java)
                                    val ok = api.saveProfile(
                                        OwnerApi.OwnerProfileRequest(
                                            nombre = nombre,
                                            telefono = telefono.ifBlank { null },
                                            direccion = direccion.ifBlank { null }
                                        )
                                    )
                                    if (ok) {
                                        Toast.makeText(context, "✓ Perfil guardado exitosamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                }
                            } catch (e: Exception) {
                                val code = (e as? HttpException)?.code()
                                val msg = when (code) {
                                    401 -> "Sesión expirada. Inicia sesión."
                                    404 -> if (isVet) "Falta /api/vet/me/profile en backend." else "Endpoint de perfil no encontrado."
                                    else -> "Error al guardar perfil: ${e.message}"
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DA581)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF7DA581)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}