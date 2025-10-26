package com.proyect.myvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyect.myvet.auth.AuthViewModel
import com.proyect.myvet.ui.theme.MyVetTheme
import com.proyect.myvet.vet.VeterinarioHomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyVetTheme {
                val navController = rememberNavController()
                val authVM: AuthViewModel = viewModel()
                val authState by authVM.state.collectAsState()

                // Forzamos validar al inicio para evitar tokens “fantasma”
                LaunchedEffect(Unit) { authVM.validateSession() }

                val startDestination = "auth_screen"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("auth_screen") { IniciosesionScreen(navController) }
                    composable("register_screen") { RegistroScreen(navController) }
                    composable(NavigationItem.Home.route) { MainScreen() }
                    composable("veterinario_home") { VeterinarioHomeScreen() }
                }

                // Si inicia sesión, enviamos al destino según rol
                LaunchedEffect(authState.isLoggedIn, authState.role) {
                    if (authState.isLoggedIn) {
                        val dest = if (authState.role == "veterinario") {
                            "veterinario_home"  // veterinario
                        } else {
                            NavigationItem.Home.route  // dueño
                        }
                        navController.navigate(dest) {
                            popUpTo("auth_screen") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                // Si cierra sesión o token inválido → volver al login y limpiar backstack
                LaunchedEffect(authState.isLoggedIn) {
                    if (!authState.isLoggedIn) {
                        navController.navigate("auth_screen") {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}