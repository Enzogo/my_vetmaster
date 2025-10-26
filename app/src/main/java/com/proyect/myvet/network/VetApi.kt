package com.proyect.myvet.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class VetOwnerDto(val id: String? = null, val nombre: String? = null, val email: String? = null)

data class VetMascotaDto(
    val id: String? = null,
    val nombre: String? = null,
    val especie: String? = null,
    val duenioNombre: String? = null,
    val ownerId: String? = null
)

data class VetCitaDto(
    val id: String? = null,
    val fechaIso: String? = null,
    val fecha: String? = null,
    val motivo: String? = null,
    val estado: String? = null,
    val mascotaId: String? = null,
    val ownerId: String? = null,
    val duenioNombre: String? = null,
    val mascotaNombre: String? = null,
    val notas: String? = null
)

data class VetProfileResponse(
    val nombre: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val clinicName: String? = null,
    val clinicPhone: String? = null,
    val clinicAddress: String? = null,
    val speciality: String? = null,
    val registrationNumber: String? = null,
    val email: String? = null
)

data class VetProfileRequest(
    val nombre: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val clinicName: String? = null,
    val clinicPhone: String? = null,
    val clinicAddress: String? = null,
    val speciality: String? = null,
    val registrationNumber: String? = null
)

data class VetCitaUpdateRequest(
    val estado: String? = null,   // "pendiente" | "en_curso" | "hecha"
    val notas: String? = null
)

interface VetApi {
    @GET("api/vet/me") suspend fun me(): VetProfileResponse

    @GET("api/vet/owners") suspend fun owners(): List<VetOwnerDto>
    @GET("api/vet/mascotas") suspend fun mascotas(): List<VetMascotaDto>
    @GET("api/vet/citas") suspend fun citas(): List<VetCitaDto>

    @POST("api/vet/me/profile")
    suspend fun saveProfile(@Body body: VetProfileRequest): Boolean

    @PATCH("api/vet/citas/{id}")
    suspend fun updateCita(@Path("id") id: String, @Body body: VetCitaUpdateRequest): VetCitaDto
}