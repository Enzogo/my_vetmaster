package com.proyect.myvet.network

import retrofit2.http.*

data class MascotaDto(
    val id: String?,
    val nombre: String?,
    val especie: String?,
    val raza: String?,
    val fechaNacimiento: String?,
    val sexo: String?
)

data class MascotaCreateRequest(
    val nombre: String,
    val especie: String,
    val raza: String?,
    val fechaNacimiento: String?,
    val sexo: String?
)

data class MascotaUpdateRequest(
    val nombre: String? = null,
    val especie: String? = null,
    val raza: String? = null,
    val fechaNacimiento: String? = null,
    val sexo: String? = null
)

data class CitaDto(
    val id: String?,
    val fechaIso: String?,
    val motivo: String?,
    val mascotaId: String?,
    val estado: String? = null
)
data class CitaCreateRequest(val fechaIso: String, val motivo: String, val mascotaId: String)
data class CitaUpdateRequest(val fechaIso: String? = null, val motivo: String? = null, val mascotaId: String? = null)

interface OwnerApi {
    data class OwnerProfileRequest(val nombre: String, val telefono: String?, val direccion: String?)
    @POST("api/owners/me/profile") suspend fun saveProfile(@Body body: OwnerProfileRequest): Boolean

    @GET("api/owners/me/mascotas") suspend fun getMyMascotas(): List<MascotaDto>
    @POST("api/owners/me/mascotas") suspend fun createMascota(@Body body: MascotaCreateRequest): MascotaDto
    @PUT("api/owners/me/mascotas/{id}") suspend fun updateMascota(@Path("id") id: String, @Body body: MascotaUpdateRequest): MascotaDto
    @DELETE("api/owners/me/mascotas/{id}") suspend fun deleteMascota(@Path("id") id: String): Boolean

    @GET("api/owners/me/citas") suspend fun getMyCitas(): List<CitaDto>
    @POST("api/owners/me/citas") suspend fun createCita(@Body body: CitaCreateRequest): CitaDto
    @PUT("api/owners/me/citas/{id}") suspend fun updateCita(@Path("id") id: String, @Body body: CitaUpdateRequest): CitaDto
    @DELETE("api/owners/me/citas/{id}") suspend fun deleteCita(@Path("id") id: String): Boolean
}