package com.proyect.myvet.vet

import android.content.Context
import com.proyect.myvet.network.CitaSummary
import com.proyect.myvet.network.MascotaSummary
import com.proyect.myvet.network.OwnerSummary
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.network.VetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VetRepository(private val context: Context) {
    private val api = RetrofitClient.authed(context).create(VetApi::class.java)

    suspend fun owners(): Result<List<OwnerSummary>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getOwners())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun citas(): Result<List<CitaSummary>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getCitas())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun mascotas(): Result<List<MascotaSummary>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getMascotas())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}