package com.proyect.myvet.auth

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAuthViewModel = staticCompositionLocalOf<AuthViewModel> {
    error("AuthViewModel no proporcionado")
}