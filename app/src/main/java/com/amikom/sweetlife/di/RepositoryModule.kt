package com.amikom.sweetlife.di

import com.amikom.sweetlife.data.remote.repository.HistoryRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.RekomenRepositoryImpl
import com.amikom.sweetlife.data.remote.retrofit.FeatureApiService
import com.amikom.sweetlife.domain.repository.RekomenRepository
import com.amikom.sweetlife.ui.screen.History.HistoryRepository
import com.amikom.sweetlife.util.AppExecutors
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryModule {
    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository
}