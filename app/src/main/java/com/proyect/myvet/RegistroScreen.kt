package com.proyect.myvet

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel

@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current
    val vm = LocalAuthViewModel.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("dueno") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registro", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            email,
            { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            password,
            { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            nombre,
            { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(8.dp))
        Row {
            RadioButton(selected = role == "dueno", onClick = { role = "dueno" })
            Text("Dueño")
            Spacer(Modifier.width(12.dp))
            RadioButton(selected = role == "veterinario", onClick = { role = "veterinario" })
            Text("Veterinario")
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                vm.register(email, password, role, nombre,
                    onSuccess = { Toast.makeText(context, "Cuenta creada", Toast.LENGTH_SHORT).show() },
                    onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrarme") }
    }
}