package com.amikom.sweetlife.data.remote.repository

import android.util.Log
import com.amikom.sweetlife.data.remote.retrofit.ChatbotApiService
import com.amikom.sweetlife.data.remote.retrofit.ChatbotRequest
import com.amikom.sweetlife.data.remote.retrofit.ChatbotResponse
import com.amikom.sweetlife.domain.repository.ChatbotRepository
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val api: ChatbotApiService
) : ChatbotRepository {
    override suspend fun sendMessage(userId: String, request: ChatbotRequest): ChatbotResponse {
        val response = api.sendMessage(userId, request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("ChatbotRepositoryImpl", "Error response: $errorBody")
            throw Exception("Failed: ${response.code()} - ${response.message()}")
        }
    }

}