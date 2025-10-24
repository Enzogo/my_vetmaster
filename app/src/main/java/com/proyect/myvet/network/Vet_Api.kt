package com.proyect.myvet.network

import retrofit2.http.GET

data class OwnerSummary(
    val id: String?,
    val nombre: String?,
    val email: String?
)

data class CitaSummary(
    val id: String?,
    val fecha: String?,         // ISO date string
    val estado: String?,        // p.ej. "confirmada"
    val duenioNombre: String?,
    val mascotaNombre: String?
)

data class MascotaSummary(
    val id: String?,
    val nombre: String?,
    val especie: String?,
    val duenioNombre: String?
)

interface VetApi {
    @GET("api/vet/owners")
    suspend fun getOwners(): List<OwnerSummary>

    @GET("api/vet/citas")
    suspend fun getCitas(): List<CitaSummary>

    @GET("api/vet/mascotas")
    suspend fun getMascotas(): List<MascotaSummary>
}