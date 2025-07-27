package com.amikom.sweetlife.data.local.entity

import androidx.datastore.preferences.protobuf.Timestamp
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
