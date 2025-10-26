package com.proyect.myvet.perfil

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel

@Composable
fun PerfilScreen(navController: NavController) {
    val authVM = LocalAuthViewModel.current
    val state by authVM.state.collectAsState()
    val isVet = state.role == "veterinario"
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (isVet) {
            // Perfil de veterinario
            Button(onClick = { navController.navigate("editar_perfil") }, modifier = Modifier.fillMaxWidth()) {
                Text("Editar perfil (Veterinario)")
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    authVM.logout()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión") }
        } else {
            // Perfil de dueño
            Button(onClick = { navController.navigate("editar_perfil") }, modifier = Modifier.fillMaxWidth()) {
                Text("Editar perfil")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("gestion_mascotas") }, modifier = Modifier.fillMaxWidth()) {
                Text("Gestionar y registrar las mascotas")
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    authVM.logout()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión") }
        }
    }
}