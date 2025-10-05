package com.proyect.myvet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.proyect.myvet.citas.CitasScreen
import com.proyect.myvet.historial.DetalleHistorialScreen
import com.proyect.myvet.historial.HistorialCita
import com.proyect.myvet.historial.HistorialScreen
import com.proyect.myvet.mascotas.RegistrarMascotaScreen
import com.proyect.myvet.perfil.GestionMascotasScreen
import com.proyect.myvet.perfil.PerfilScreen
import com.proyect.myvet.prediagnostico.PrediagnosticoScreen
import com.proyect.myvet.ui.theme.MyVetTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                listOf(NavigationItem.Home, NavigationItem.Citas, NavigationItem.Prediagnostico, NavigationItem.Historial, NavigationItem.Perfil).forEach { item ->
                    val isSelected = currentRoute?.startsWith(item.route) == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = NavigationItem.Home.route, Modifier.padding(innerPadding)) {
            composable(NavigationItem.Home.route) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bienvenido A My Vet") } }
            composable(
                route = "${NavigationItem.Citas.route}?motivo={motivo}",
                arguments = listOf(navArgument("motivo") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) {
                CitasScreen(navController = navController, motivoInicial = it.arguments?.getString("motivo"))
            }
            composable(NavigationItem.Prediagnostico.route) { PrediagnosticoScreen(navController = navController) }
            composable(NavigationItem.Historial.route) { HistorialScreen(navController = navController) }
            composable(
                route = "detalle_historial/{citaJson}",
                arguments = listOf(navArgument("citaJson") { type = NavType.StringType })
            ) {
                val citaJson = it.arguments?.getString("citaJson")?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                val cita = Gson().fromJson(citaJson, HistorialCita::class.java)
                DetalleHistorialScreen(navController = navController, cita = cita)
            }
            composable(NavigationItem.Perfil.route) { PerfilScreen(navController = navController) }

            // --- ¡NUEVAS RUTAS! ---
            composable("gestion_mascotas") {
                GestionMascotasScreen(navController = navController)
            }
            composable(
                route = "registrar_mascota?mascotaId={mascotaId}",
                arguments = listOf(navArgument("mascotaId") { type = NavType.StringType; nullable = true })
            ) {
                val mascotaId = it.arguments?.getString("mascotaId")?.toLongOrNull()
                RegistrarMascotaScreen(navController = navController, mascotaId = mascotaId)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaInicial() {
    MyVetTheme {
        MainScreen()
    }
}