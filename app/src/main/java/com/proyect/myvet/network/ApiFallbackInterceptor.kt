package com.proyect.myvet.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor que reintenta automáticamente rutas alternando el prefijo "/api" si recibe 404.
 * - /auth/... -> reintenta /api/auth/...
 * - /api/auth/... -> reintenta /auth/...
 */
class ApiFallbackInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalReq = chain.request()
        val originalRes = chain.proceed(originalReq)

        if (originalRes.code != 404) {
            return originalRes
        }

        // Solo reintentar una vez alternando el prefijo "/api"
        val url = originalReq.url
        val path = url.encodedPath

        val altPath = when {
            path.startsWith("/api/") -> path.removePrefix("/api")
            else -> "/api$path"
        }

        // Si no cambia el path, no reintentamos
        if (altPath == path) {
            return originalRes
        }

        // Cerramos la respuesta 404 antes de reintentar para evitar leaks
        originalRes.close()

        val newUrl = url.newBuilder()
            .encodedPath(altPath)
            .build()

        val newReq = originalReq.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newReq)
    }
}