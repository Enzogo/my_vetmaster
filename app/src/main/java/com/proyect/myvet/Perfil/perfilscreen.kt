package com.proyect.myvet.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PerfilScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Perfil de Usuario", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para registrar una nueva mascota
        Button(
            onClick = { navController.navigate("registrar_mascota") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.Add, "Añadir Mascota", modifier = Modifier.padding(end = 8.dp))
            Text("Registrar Nueva Mascota")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ¡NUEVO BOTÓN! ---
        // Botón para gestionar las mascotas existentes
        Button(
            onClick = { navController.navigate("gestion_mascotas") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.Pets, "Gestionar Mascotas", modifier = Modifier.padding(end = 8.dp))
            Text("Gestionar Mis Mascotas")
        }
    }
}