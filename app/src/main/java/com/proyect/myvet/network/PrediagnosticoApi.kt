package com.proyect.myvet.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class PrediagnosticoRequest(
    val sintomas: String,
    val especie: String? = null,
    val edad: String? = null,
    val contexto: String? = null
)

data class PrediagnosticoParsed(
    val recomendaciones: String? = null,
    val red_flags: String? = null,
    val confidence: String? = null,
    val fuentes: List<String>? = null,
    val disclaimer: String? = null,
    val urgencia: String? = null,
    val animal: String? = null,
    val causas_frecuentes: List<String>? = null
)

data class PrediagnosticoResp(
    val ok: Boolean? = null,
    val consultId: String? = null,
    val parsed: PrediagnosticoParsed? = null,
    val raw: String? = null
)

interface PrediagnosticoApi {
    @POST("api/ai/agent")
    suspend fun getPrediagnostico(@Body req: PrediagnosticoRequest): Response<PrediagnosticoResp>
}