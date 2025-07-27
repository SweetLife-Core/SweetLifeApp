package com.amikom.sweetlife.data.remote.repository

import android.util.Log
import com.amikom.sweetlife.data.local.dao.ChatDao
import com.amikom.sweetlife.data.local.entity.ChatMessageEntity
import com.amikom.sweetlife.data.remote.retrofit.ChatbotApiService
import com.amikom.sweetlife.data.remote.retrofit.ChatbotRequest
import com.amikom.sweetlife.data.remote.retrofit.ChatbotResponse
import com.amikom.sweetlife.domain.repository.ChatbotRepository
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val api: ChatbotApiService,
    private val dao : ChatDao
) : ChatbotRepository {
    val timestamp = System.currentTimeMillis()
    override suspend fun sendMessage(userId: String, request: ChatbotRequest): ChatbotResponse {
        dao.insertMessage(ChatMessageEntity(message = request.msg, isFromUser = true, timestamp = timestamp))
        val response = api.sendMessage(userId, request)
        if (response.isSuccessful) {
            dao.insertMessage(ChatMessageEntity(message = response.body()?.output ?: "No response", isFromUser = false, timestamp = System.currentTimeMillis()))
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("ChatbotRepositoryImpl", "Error response: $errorBody")
            throw Exception("Failed: ${response.code()} - ${response.message()}")
        }
    }
    suspend fun getHistory(): List<ChatMessageEntity> {
        return dao.getAllMessages()
    }

}