package com.proyect.myvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyect.myvet.auth.AuthViewModel
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.ui.theme.MyVetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyVetTheme {
                val authVM: AuthViewModel = viewModel()
                val authState by authVM.state.collectAsState()
                val navController = rememberNavController()

                CompositionLocalProvider(LocalAuthViewModel provides authVM) {
                    LaunchedEffect(Unit) { authVM.validateSession() }

                    NavHost(navController = navController, startDestination = "auth_screen") {
                        composable("auth_screen") { InicioSesionScreen(navController) }
                        composable("register_screen") { RegistroScreen(navController) }
                        composable("app") { MainScreen() }
                    }

                    LaunchedEffect(authState.isLoggedIn, authState.role) {
                        if (authState.isLoggedIn) {
                            navController.navigate("app") {
                                popUpTo("auth_screen") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
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
}