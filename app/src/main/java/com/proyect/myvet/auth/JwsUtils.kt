package com.proyect.myvet.auth

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    // Retorna true si el token está expirado o es inválido
    fun isJwtExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return true // token mal formado
            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payload = JSONObject(String(payloadBytes, Charsets.UTF_8))
            val exp = payload.optLong("exp", 0L) // en segundos desde epoch

            // Si el backend no envía 'exp', consideramos el token como expirado para forzar login
            if (exp == 0L) return true

            val now = System.currentTimeMillis() / 1000
            now >= exp
        } catch (_: Exception) {
            true // cualquier error => tratar como expirado
        }
    }
}