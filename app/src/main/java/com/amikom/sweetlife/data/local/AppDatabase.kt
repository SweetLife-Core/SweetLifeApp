package com.amikom.sweetlife.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amikom.sweetlife.data.local.dao.ChatDao
import com.amikom.sweetlife.data.local.entity.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
