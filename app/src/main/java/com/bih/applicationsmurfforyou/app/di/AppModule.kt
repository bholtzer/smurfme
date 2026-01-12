package com.bih.applicationsmurfforyou.app.di

import com.bih.applicationsmurfforyou.data.repository.ImagenRepository
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.bih.applicationsmurfforyou.domain.usecase.GetAllSmurfsUseCase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGetAllSmurfsUseCase(repo: SmurfRepository) = GetAllSmurfsUseCase(repo)

    @Provides
    @Singleton
    fun provideImagenRepository(): ImagenRepository {
        return ImagenRepository()
    }
}


