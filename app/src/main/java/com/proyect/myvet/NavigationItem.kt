package com.proyect.myvet

import androidx.annotation.DrawableRes

sealed class NavigationItem(val route: String, @DrawableRes val icon: Int, val title: String) {
    object Home : NavigationItem("home", R.drawable.logo_myvet, "Inicio")
    object Citas : NavigationItem("citas", R.drawable.citas, "Citas")
    object Prediagnostico : NavigationItem("prediagnostico", R.drawable.ic_launcher_foreground, "Pre-diagnóstico")
    object Historial : NavigationItem("historial", R.drawable.historial, "Historial")
    object Perfil : NavigationItem("perfil", R.drawable.perfil , "Perfil")
}