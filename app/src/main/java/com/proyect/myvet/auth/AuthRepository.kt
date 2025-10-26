package com.proyect.myvet.auth

import android.content.Context
import com.proyect.myvet.network.AuthApi
import com.proyect.myvet.network.LoginRequest
import com.proyect.myvet.network.RegisterRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val api = RetrofitClient.instance.create(AuthApi::class.java)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()
    fun getToken(): String? = prefs.getString("token", null)
    fun getRole(): String? = prefs.getString("role", null)
    fun getEmail(): String? = prefs.getString("email", null)
    fun getNombre(): String? = prefs.getString("nombre", null)

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.login(LoginRequest(email, password))
            if (!resp.isSuccessful || resp.body() == null) return@withContext Result.failure(Exception("Login falló"))
            val body = resp.body()!!
            prefs.edit()
                .putString("token", body.token)
                .putString("role", body.user.role)
                .putString("email", body.user.email)
                .putString("nombre", body.user.nombre)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, role: String, nombre: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.register(RegisterRequest(email, password, role, nombre))
            if (!resp.isSuccessful || resp.body() == null) return@withContext Result.failure(Exception("Registro falló"))
            val body = resp.body()!!
            prefs.edit()
                .putString("token", body.token)
                .putString("role", body.user.role)
                .putString("email", body.user.email)
                .putString("nombre", body.user.nombre)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}