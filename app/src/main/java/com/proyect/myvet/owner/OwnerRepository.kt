package com.proyect.myvet.owner

import android.content.Context
import com.proyect.myvet.network.CitaCreateRequest
import com.proyect.myvet.network.CitaDto
import com.proyect.myvet.network.MascotaCreateRequest
import com.proyect.myvet.network.MascotaDto
import com.proyect.myvet.network.OwnerApi
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OwnerRepository(private val context: Context) {
    private val api = RetrofitClient.authed(context).create(OwnerApi::class.java)

    suspend fun loadMascotas(): Result<List<MascotaDto>> = withContext(Dispatchers.IO) {
        try { Result.success(api.getMyMascotas()) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun loadCitas(): Result<List<CitaDto>> = withContext(Dispatchers.IO) {
        try { Result.success(api.getMyCitas()) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addMascota(
        nombre: String,
        especie: String,
        raza: String?,
        fechaNacimiento: String?,
        sexo: String?
    ): Result<MascotaDto> = withContext(Dispatchers.IO) {
        try {
            Result.success(
                api.createMascota(
                    MascotaCreateRequest(
                        nombre = nombre,
                        especie = especie,
                        raza = raza?.takeIf { it.isNotBlank() },
                        fechaNacimiento = fechaNacimiento?.takeIf { it.isNotBlank() },
                        sexo = sexo?.takeIf { it.isNotBlank() }
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCita(fechaIso: String, motivo: String, mascotaId: String): Result<CitaDto> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(api.createCita(CitaCreateRequest(fechaIso, motivo, mascotaId)))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateMascota(
        id: String,
        nombre: String?,
        especie: String?,
        raza: String?,
        fechaNacimiento: String?,
        sexo: String?
    ): Result<MascotaDto> = withContext(Dispatchers.IO) {
        try {
            Result.success(
                api.updateMascota(
                    id,
                    com.proyect.myvet.network.MascotaUpdateRequest(
                        nombre = nombre,
                        especie = especie,
                        raza = raza,
                        fechaNacimiento = fechaNacimiento,
                        sexo = sexo
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMascota(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.deleteMascota(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCita(
        id: String,
        fechaIso: String?,
        motivo: String?,
        mascotaId: String?
    ): Result<CitaDto> = withContext(Dispatchers.IO) {
        try {
            Result.success(
                api.updateCita(
                    id,
                    com.proyect.myvet.network.CitaUpdateRequest(
                        fechaIso = fechaIso,
                        motivo = motivo,
                        mascotaId = mascotaId
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCita(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.deleteCita(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}