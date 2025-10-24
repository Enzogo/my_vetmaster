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
import com.proyect.myvet.citas.CitasScreen
import com.proyect.myvet.mascotas.GestionMascotasScreen
import com.proyect.myvet.mascotas.RegistrarMascotaScreen
import com.proyect.myvet.perfil.EditarPerfilDuenoScreen
import com.proyect.myvet.ui.theme.MyVetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyVetTheme {
                val navController = rememberNavController()
                val authVM: AuthViewModel = viewModel()
                val authState by authVM.state.collectAsState()

                val startDestination = "auth_screen"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("auth_screen") { IniciosesionScreen(navController) }
                    composable(NavigationItem.Home.route) { MainScreen() }
                    composable("editar_perfil") { EditarPerfilDuenoScreen(navController) }
                    composable("registrar_mascota") { RegistrarMascotaScreen(navController) }
                    composable("gestion_mascotas") { GestionMascotasScreen(navController) }
                    composable(NavigationItem.Citas.route) { CitasScreen(navController) }
                }

                LaunchedEffect(authState.isLoggedIn) {
                    if (authState.isLoggedIn) {
                        navController.navigate(NavigationItem.Home.route) {
                            popUpTo("auth_screen") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}