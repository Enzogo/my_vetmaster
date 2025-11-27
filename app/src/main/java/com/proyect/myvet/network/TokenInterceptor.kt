package com.proyect.myvet.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val context: Context) : Interceptor {
    companion object {
        private const val TAG = "TokenInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val rawToken = prefs.getString("token", null)
        val token = rawToken?.trim()
        val original = chain.request()

        val builder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
            Log.d(TAG, "Token presente y añadido al header Authorization")
        } else {
            Log.d(TAG, "No hay token en prefs")
        }

        val req = builder.build()
        return chain.proceed(req)
    }
}