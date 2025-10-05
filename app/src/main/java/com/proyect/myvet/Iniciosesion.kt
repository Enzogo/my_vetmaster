package com.proyect.myvet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    // --- ESTADO DE LA PANTALLA ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Estado para saber si estamos en modo "Registro" o "Login"
    var isRegisterMode by remember { mutableStateOf(false) }
    // Estado para la selección de rol en el modo registro
    val roles = listOf("Dueño", "Veterinario")
    var selectedRole by remember { mutableStateOf(roles[0]) }

    // --- DISEÑO DE LA PANTALLA ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        // --- SECCIÓN QUE SOLO APARECE EN MODO REGISTRO ---
        if (isRegisterMode) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Selecciona tu rol:", style = MaterialTheme.typography.bodyLarge)
            Row(Modifier.fillMaxWidth()) {
                roles.forEach { role ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (selectedRole == role),
                                onClick = { selectedRole = role }
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedRole == role),
                            onClick = { selectedRole = role }
                        )
                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Por ahora, la lógica es simple:
                // Imprimimos los datos y navegamos a la pantalla principal.
                if (isRegisterMode) {
                    println("Registrando: Email=$email, Pass=$password, Rol=$selectedRole")
                } else {
                    println("Iniciando Sesión: Email=$email, Pass=$password")
                }

                // Navegamos a la pantalla principal y limpiamos el historial
                // para que el usuario no pueda volver atrás.
                navController.navigate("main_screen") {
                    popUpTo("auth_screen") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (isRegisterMode) "Registrarse" else "Entrar", fontSize = 18.sp)
        }

        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
            Text(if (isRegisterMode) "¿Ya tienes cuenta? Inicia Sesión" else "¿No tienes cuenta? Regístrate")
        }
    }
}