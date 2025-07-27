package com.amikom.sweetlife.di


import android.content.Context
import androidx.room.Room
import com.amikom.sweetlife.data.local.AppDatabase
import com.amikom.sweetlife.data.local.dao.ChatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sweetlife_database"
        )
            .fallbackToDestructiveMigration() // 🔥 menghapus dan buat ulang DB jika schema berubah
            .build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: AppDatabase): ChatDao {
        return database.chatDao()
    }
}
