package com.proyect.myvet.network

import retrofit2.Response
import retrofit2.http.*

data class MascotaDto(
    val id: String?,
    val nombre: String?,
    val especie: String?,
    val raza: String?,
    val fechaNacimiento: String?,
    val sexo: String?,
    val edad: Int?
)

data class MascotaCreateRequest(
    val nombre: String,
    val especie: String,
    val raza: String? = null,
    val fechaNacimiento: String? = null,
    val sexo: String? = null,
    val edad: Int? = null
)

data class MascotaUpdateRequest(
    val nombre: String? = null,
    val especie: String? = null,
    val raza: String? = null,
    val fechaNacimiento: String? = null,
    val sexo: String? = null,
    val edad: Int? = null
)

data class MascotaResponse(
    val id: String,
    val nombre: String,
    val especie: String,
    val raza: String?,
    val fechaNacimiento: String?,
    val sexo: String?,
    val edad: Int?
)

data class CitaDto(
    val id: String?,
    val fechaIso: String?,
    val motivo: String?,
    val mascotaId: String?,
    val estado: String? = null,
    val notas: String? = null
)

data class CitaCreateRequest(
    val fechaIso: String,
    val motivo: String,
    val mascotaId: String
)

data class CitaUpdateRequest(
    val fechaIso: String? = null,
    val motivo: String? = null,
    val mascotaId: String? = null
)

data class OwnerProfileResponse(
    val nombre: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val email: String? = null
)

interface OwnerApi {
    data class OwnerProfileRequest(
        val nombre: String,
        val telefono: String?,
        val direccion: String?
    )

    @GET("api/owners/me")
    suspend fun me(): OwnerProfileResponse

    @POST("api/owners/me/profile")
    suspend fun saveProfile(@Body body: OwnerProfileRequest): Boolean

    @GET("api/owners/me/mascotas")
    suspend fun getMyMascotas(): List<MascotaDto>

    @GET("api/owners/me/mascotas")
    suspend fun listMyMascotas(): Response<List<MascotaResponse>>

    @POST("api/owners/me/mascotas")
    suspend fun createMascota(@Body req: MascotaCreateRequest): Response<MascotaResponse>

    @PUT("api/owners/me/mascotas/{id}")
    suspend fun updateMascota(@Path("id") id: String, @Body req: MascotaUpdateRequest): MascotaDto

    @DELETE("api/owners/me/mascotas/{id}")
    suspend fun deleteMascota(@Path("id") id: String)

    @GET("api/owners/me/citas")
    suspend fun getMyCitas(): List<CitaDto>

    // Cambiado para devolver Response<CitaDto>
    @POST("api/owners/me/citas")
    suspend fun createCita(@Body req: CitaCreateRequest): Response<CitaDto>

    @PUT("api/owners/me/citas/{id}")
    suspend fun updateCita(@Path("id") id: String, @Body req: CitaUpdateRequest): CitaDto

    @DELETE("api/owners/me/citas/{id}")
    suspend fun deleteCita(@Path("id") id: String)
}