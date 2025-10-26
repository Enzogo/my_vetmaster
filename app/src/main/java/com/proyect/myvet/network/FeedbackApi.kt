package com.proyect.myvet.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class FeedbackRequest(
    val calificacion: Int,  // 1-5 rating
    val sugerencias: String?
)

data class FeedbackDto(
    val id: String?,
    val userId: String?,
    val calificacion: Int?,
    val sugerencias: String?,
    val createdAt: String?
)

interface FeedbackApi {
    @POST("api/feedback")
    suspend fun submitFeedback(@Body body: FeedbackRequest): FeedbackDto

    @GET("api/feedback/mine")
    suspend fun getMyFeedback(): List<FeedbackDto>
}
