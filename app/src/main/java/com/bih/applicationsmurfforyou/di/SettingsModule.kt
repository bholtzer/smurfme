package com.bih.applicationsmurfforyou.di

import com.bih.applicationsmurfforyou.data.repository.SettingsLocalDataSource
import com.bih.applicationsmurfforyou.data.repository.SettingsRepositoryImpl
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        localDataSource: SettingsLocalDataSource
    ): SettingsRepository {
        return SettingsRepositoryImpl(localDataSource)
    }
}
