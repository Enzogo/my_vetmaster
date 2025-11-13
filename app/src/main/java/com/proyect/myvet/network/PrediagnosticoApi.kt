package com.proyect.myvet.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Request que envía la app al backend
data class PrediagnosticoRequest(
    val sintomas: String,
    val especie: String? = null,
    val edad: String? = null,
    val sexo: String? = null
)

// Respuesta esperada del backend (según routes/ai.js)
data class PrediagnosticoResponse(
    val recomendaciones: String? = null,
    val red_flags: String? = null,
    val disclaimer: String? = null,
    val _model: String? = null // si el backend devuelve el modelo usado
)

// Envelope si el backend devuelve otro formato (ajusta si tu backend devuelve distinta estructura)
data class PrediagnosticoEnvelope(
    val recomendaciones: String?,
    val red_flags: String?,
    val disclaimer: String?,
    val _model: String?
)

interface PrediagnosticoApi {
    @POST("api/ai/prediagnostico")
    suspend fun getPrediagnostico(@Body req: PrediagnosticoRequest): Response<PrediagnosticoEnvelope>
}