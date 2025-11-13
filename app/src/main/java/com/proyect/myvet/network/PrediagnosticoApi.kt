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
    val recomendaciones: String?,
    val red_flags: String?,
    val confidence: String?,
    val fuentes: List<String>?,
    val disclaimer: String?
)

data class PrediagnosticoResp(
    val ok: Boolean?,
    val consultId: String?,
    val parsed: PrediagnosticoParsed?,
    val raw: String?
)

interface PrediagnosticoApi {
    @POST("api/ai/agent")
    suspend fun getPrediagnostico(@Body req: PrediagnosticoRequest): Response<PrediagnosticoResp>
}