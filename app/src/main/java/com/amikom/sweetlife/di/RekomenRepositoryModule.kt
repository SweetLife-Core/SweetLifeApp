// RekomenRepositoryModule.kt
package com.amikom.sweetlife.di

import com.amikom.sweetlife.domain.repository.RekomenRepository
import com.amikom.sweetlife.data.remote.repository.RekomenRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RekomenRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRekomenRepository(
        impl: RekomenRepositoryImpl
    ): RekomenRepository
}