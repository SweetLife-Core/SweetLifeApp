package com.amikom.sweetlife.domain.repository

import com.amikom.sweetlife.data.remote.retrofit.ChatbotRequest
import com.amikom.sweetlife.data.remote.retrofit.ChatbotResponse

interface ChatbotRepository {
    suspend fun sendMessage(userId: String, request: ChatbotRequest): ChatbotResponse
}
