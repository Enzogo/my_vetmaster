package com.proyect.myvet.auth

import android.content.Context
import com.proyect.myvet.network.AuthApi
import com.proyect.myvet.network.AuthResponse
import com.proyect.myvet.network.LoginRequest
import com.proyect.myvet.network.RegisterRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {

    private val prefs by lazy { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }
    private val api by lazy { RetrofitClient.instance.create(AuthApi::class.java) }

    suspend fun register(
        email: String,
        password: String,
        role: String,
        nombre: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.register(RegisterRequest(email.trim(), password, role, nombre))
            if (!resp.isSuccessful) {
                val msg = "HTTP ${resp.code()} ${resp.errorBody()?.string() ?: ""}".trim()
                return@withContext Result.failure(Exception(msg))
            }
            val body = resp.body() ?: return@withContext Result.failure(Exception("Respuesta vacía"))
            saveAuth(body)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.login(LoginRequest(email.trim(), password))
            if (!resp.isSuccessful) {
                val msg = "HTTP ${resp.code()} ${resp.errorBody()?.string() ?: ""}".trim()
                return@withContext Result.failure(Exception(msg))
            }
            val body = resp.body() ?: return@withContext Result.failure(Exception("Respuesta vacía"))
            saveAuth(body)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    // Ahora valida expiración y limpia si es inválido
    fun isLoggedIn(): Boolean {
        val token = prefs.getString("token", null) ?: return false
        val expired = JwtUtils.isJwtExpired(token)
        if (expired) {
            logout()
            return false
        }
        return true
    }

    fun getRole(): String? = prefs.getString("role", null)
    fun getToken(): String? = prefs.getString("token", null)
    fun getEmail(): String? = prefs.getString("email", null)
    fun getNombre(): String? = prefs.getString("nombre", null)
    fun getUserId(): String? = prefs.getString("userId", null)

    private fun saveAuth(body: AuthResponse) {
        prefs.edit()
            .putString("token", body.token)
            .putString("email", body.user.email)
            .putString("nombre", body.user.nombre ?: "")
            .putString("role", body.user.role)
            .putString("userId", body.user.id)
            .apply()
    }
}