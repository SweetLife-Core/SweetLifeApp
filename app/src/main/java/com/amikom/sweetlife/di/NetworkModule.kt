package com.amikom.sweetlife.di

import com.amikom.sweetlife.BuildConfig
import com.amikom.sweetlife.data.remote.retrofit.ChatbotApiService
import com.amikom.sweetlife.data.remote.repository.ChatbotRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.MiniCourseRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.MiniGroceryRepositoryImpl
import com.amikom.sweetlife.domain.repository.ChatbotRepository
import com.amikom.sweetlife.domain.repository.GroceryRepository
import com.amikom.sweetlife.domain.repository.MiniCourseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MiniCourseModule {
    @Binds
    abstract fun bindMiniCourseRepository(
        impl: MiniCourseRepositoryImpl
    ): MiniCourseRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MiniGroceryModule {
    @Binds
    abstract fun bindMiniGroceryRepository(
        impl: MiniGroceryRepositoryImpl
    ): GroceryRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindChatbotRepository(
        impl: ChatbotRepositoryImpl
    ): ChatbotRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_CHAT)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideChatbotApiService(retrofit: Retrofit): ChatbotApiService =
        retrofit.create(ChatbotApiService::class.java)
}