package com.proyect.myvet.auth

import android.content.Context
import android.util.Log
import com.proyect.myvet.network.AuthApi
import com.proyect.myvet.network.LoginRequest
import com.proyect.myvet.network.RegisterRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
            if (!resp.isSuccessful || resp.body() == null) return@withContext Result.failure(Exception("Credenciales inválidas"))
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
            if (resp.isSuccessful && resp.body() != null) {
                val body = resp.body()!!
                prefs.edit()
                    .putString("token", body.token)
                    .putString("role", body.user.role)
                    .putString("email", body.user.email)
                    .putString("nombre", body.user.nombre)
                    .apply()
                return@withContext Result.success(Unit)
            }

            // No fue exitoso: extraer mensaje del server (si lo devuelve) y loguear
            val errorBodyString = try { resp.errorBody()?.string() } catch (_: Exception) { null }
            Log.e("AuthRepository", "register failed: code=${resp.code()} message=${resp.message()} errorBody=$errorBodyString")

            // Intentar obtener campo "message" del JSON devuelto por el backend
            val serverMsg = try {
                if (!errorBodyString.isNullOrBlank()) {
                    val obj = JSONObject(errorBodyString)
                    when {
                        obj.has("message") -> obj.getString("message")
                        obj.has("error") -> obj.getString("error")
                        else -> null
                    }
                } else null
            } catch (_: Exception) {
                null
            }

            val finalMsg = serverMsg ?: "Registro falló (código ${resp.code()})"
            return@withContext Result.failure(Exception(finalMsg))
        } catch (e: Exception) {
            Log.e("AuthRepository", "register exception", e)
            Result.failure(e)
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}