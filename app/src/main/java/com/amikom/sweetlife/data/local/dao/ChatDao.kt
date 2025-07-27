package com.amikom.sweetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.amikom.sweetlife.data.local.entity.ChatMessageEntity

@Dao
interface ChatDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages")
    suspend fun getAllMessages(): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages")
    suspend fun clearMessages()
}
