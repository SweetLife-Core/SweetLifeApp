package com.amikom.sweetlife.ui.screen.Chatbot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amikom.sweetlife.data.remote.repository.ChatbotRepositoryImpl
import com.amikom.sweetlife.data.remote.retrofit.ChatbotRequest
import com.amikom.sweetlife.domain.repository.ChatbotRepository
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)


@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val repository: ChatbotRepositoryImpl,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _response = MutableStateFlow<String?>(null)
    val response: StateFlow<String?> = _response
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory
    private val _isBotTyping = MutableStateFlow(false)
    val isBotTyping: StateFlow<Boolean> = _isBotTyping

    init {
        viewModelScope.launch {
            val localHistory = repository.getHistory().map {
                ChatMessage(it.message, it.isFromUser, it.timestamp)
            }
            _chatHistory.value = localHistory
        }
    }

    fun sendMessage(msg: String) {
        viewModelScope.launch {
            _chatHistory.value += ChatMessage(msg, isFromUser = true)
            authUseCases.readUserAllToken().collect { tokenList ->
                val userId = tokenList.firstOrNull { it.first == "userToken" }?.second
                if (userId.isNullOrEmpty()) {
                    _response.value = "Error: User ID is missing"
                    return@collect
                }
                try {
                    _isBotTyping.value = true
                    val result = repository.sendMessage(userId, ChatbotRequest(msg))
                    delay(300)
                    _chatHistory.value += ChatMessage(result.output, isFromUser = false)
                    _response.value = result.output
                    _isBotTyping.value = false
                } catch (e: Exception) {
                    _response.value = "Error: ${e.localizedMessage ?: "Unknown error"}"
                }
            }
        }
    }

}
