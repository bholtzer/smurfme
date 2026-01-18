package com.bih.applicationsmurfforyou.app.di

import com.bih.applicationsmurfforyou.data.repository.SmurfRepositoryImpl
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSmurfRepository(): SmurfRepository {
        return SmurfRepositoryImpl()
    }
}
