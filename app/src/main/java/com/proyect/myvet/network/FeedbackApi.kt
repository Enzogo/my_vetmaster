package com.proyect.myvet.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class FeedbackRequest(
    val rating: Int,        // 1-5 stars
    val suggestion: String? // Optional text feedback
)

data class FeedbackDto(
    val id: String,
    val userId: String,
    val rating: Int,
    val suggestion: String?,
    val createdAt: String
)

interface FeedbackApi {
    @POST("api/feedback")
    suspend fun submitFeedback(@Body body: FeedbackRequest): FeedbackDto

    @GET("api/feedback/mine")
    suspend fun getMyFeedback(): List<FeedbackDto>
}
