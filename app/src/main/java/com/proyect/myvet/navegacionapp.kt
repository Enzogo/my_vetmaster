package com.proyect.myvet

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyect.myvet.vet.VeterinarioHomeScreen
import com.proyect.myvet.perfil.PerfilScreen
import com.proyect.myvet.citas.CitasScreen
import com.proyect.myvet.perfil.GestionMascotasScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth_screen"
    ) {
        composable("auth_screen") { IniciosesionScreen(navController) }

        // Home de Dueño (tu pantalla actual)
        composable("main_screen") { MainScreen() }

        // Veterinario
        composable("vet_home") { VeterinarioHomeScreen() }

        // Perfil de Dueño (si navegas desde MainScreen a esta)
        composable("perfil_dueno") { PerfilScreen(navController) }

        // Usar tu pantalla existente de Citas para registrar
        composable("registrar_cita") { CitasScreen(navController) }

        // Si tienes una pantalla de gestión de mascotas con esta ruta,
        // puedes definir aquí su destino:
        composable("gestion_mascotas") { GestionMascotasScreen(navController) }
    }
}