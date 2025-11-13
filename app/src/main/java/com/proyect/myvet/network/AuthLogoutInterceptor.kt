package com.proyect.myvet.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthLogoutInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 401) {
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            // La UI  debe reaccionar a que token desapareció y navegar a login.
        }
        return response
    }
}