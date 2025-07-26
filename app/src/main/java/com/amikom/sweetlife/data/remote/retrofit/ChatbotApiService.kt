package com.amikom.sweetlife.data.remote.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class ChatbotRequest(val msg : String)
data class ChatbotResponse(val output: String)

interface ChatbotApiService {
    @POST("webhook/sweetlife-chatbot")
    suspend fun sendMessage(
        @Header("id") userId: String,
        @Body request: ChatbotRequest
    ): Response<ChatbotResponse>
}

