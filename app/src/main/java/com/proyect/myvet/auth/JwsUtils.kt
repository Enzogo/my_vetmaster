package com.proyect.myvet.auth

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun isJwtExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return true // token malformado = inválido
            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val payload = JSONObject(String(payloadBytes, Charsets.UTF_8))
            val exp = payload.optLong("exp", 0L) // segundos
            if (exp == 0L) {
                // Si el backend no envía 'exp', tratamos el token como válido
                // Cambia a 'return true' si quieres forzar login en cada arranque sin exp
                return false
            }
            val now = System.currentTimeMillis() / 1000
            now >= exp
        } catch (_: Exception) {
            true // cualquier error al parsear => lo tratamos como expirado
        }
    }
}