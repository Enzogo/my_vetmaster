package com.proyect.myvet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val title: String, val icon: ImageVector) {
    // Dueño
    data object Home : NavigationItem("home", "Inicio", Icons.Filled.Home)
    data object Citas : NavigationItem("citas", "Citas", Icons.Filled.CalendarToday)
    data object Prediagnostico : NavigationItem("prediagnostico", "IA", Icons.Filled.MedicalServices)
    data object Historial : NavigationItem("historial", "Historial", Icons.Filled.List)
    data object Perfil : NavigationItem("perfil", "Perfil", Icons.Filled.Person)

    // Veterinario
    data object VetCitas : NavigationItem("vet_citas", "Citas", Icons.Filled.CalendarToday)
    data object VetPerfil : NavigationItem("vet_perfil", "Mi Perfil", Icons.Filled.Person)
}