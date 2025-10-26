package com.proyect.myvet.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val email: String,
    val password: String,
    val role: String,          // "dueno" o "veterinario"
    val nombre: String? = null
)

data class LoginRequest(val email: String, val password: String)

data class UserDto(
    val id: String,
    val email: String,
    val role: String,
    val nombre: String?
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>
}