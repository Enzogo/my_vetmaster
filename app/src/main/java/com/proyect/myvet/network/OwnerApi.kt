package com.proyect.myvet.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class MascotaDto(
    val id: String?,
    val nombre: String?,
    val especie: String?,
    val raza: String?
)

data class MascotaCreateRequest(
    val nombre: String,
    val especie: String,
    val raza: String?,
    val fechaNacimiento: String?,
    val sexo: String?
)

data class CitaDto(
    val id: String?,
    val fechaIso: String?,
    val motivo: String?,
    val mascotaId: String?,
    val estado: String? = null
)

data class CitaCreateRequest(
    val fechaIso: String,
    val motivo: String,
    val mascotaId: String
)

interface OwnerApi {
    data class OwnerProfileRequest(
        val nombre: String,
        val telefono: String?,
        val direccion: String?
    )

    @POST("api/owners/me/profile")
    suspend fun saveProfile(@Body body: OwnerProfileRequest): Boolean

    @GET("api/owners/me/mascotas")
    suspend fun getMyMascotas(): List<MascotaDto>

    @POST("api/owners/me/mascotas")
    suspend fun createMascota(@Body body: MascotaCreateRequest): MascotaDto

    @GET("api/owners/me/citas")
    suspend fun getMyCitas(): List<CitaDto>

    @POST("api/owners/me/citas")
    suspend fun createCita(@Body body: CitaCreateRequest): CitaDto
}