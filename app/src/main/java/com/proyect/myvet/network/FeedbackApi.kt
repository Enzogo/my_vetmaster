package com.proyect.myvet.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class FeedbackDto(val id: String, val rating: Int, val sugerencia: String, val createdAt: String)
data class FeedbackCreateRequest(val rating: Int, val sugerencia: String)
data class FeedbackSummary(val avg: Double, val count: Int)

interface FeedbackApi {
    // Acepta cualquier respuesta 200/201 sin depender del cuerpo exacto
    @POST("api/feedback")
    suspend fun create(@Body body: FeedbackCreateRequest): Response<Unit>

    @GET("api/feedback/mine")
    suspend fun mine(): List<FeedbackDto>

    // Resumen global (promedio + cantidad)
    @GET("api/feedback/summary")
    suspend fun summary(): FeedbackSummary
}