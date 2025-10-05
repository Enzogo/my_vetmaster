package com.proyect.myvet

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    // Este es el controlador de navegación principal de TODA la aplicación.
    val navController = rememberNavController()

    // NavHost es el contenedor que intercambia las pantallas.
    NavHost(navController = navController, startDestination = "auth_screen") {

        // Definimos la ruta para la pantalla de autenticación.
        composable("auth_screen") {
            AuthScreen(navController = navController)
        }

        // Definimos la ruta para la pantalla principal (la que ya tenías).
        composable("main_screen") {
            MainScreen()
        }
    }
}