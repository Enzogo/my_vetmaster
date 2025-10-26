package com.proyect.myvet.network

import retrofit2.http.Body
import retrofit2.http.POST

data class PrediRequest(val sintomas: String, val especie: String? = null, val edad: String? = null, val sexo: String? = null)
data class PrediResponse(val recomendaciones: String, val red_flags: String?, val disclaimer: String)

interface PrediagnosticoApi {
    @POST("api/ai/prediagnostico")
    suspend fun predi(@Body body: PrediRequest): PrediResponse
}