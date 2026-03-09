package com.bih.applicationsmurfforyou.app.di

import com.bih.applicationsmurfforyou.data.ai.ImagenModelConfiguration
import com.bih.applicationsmurfforyou.data.repository.SmurfRemoteDataSource
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.type.PublicPreviewAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmurfRemoteModule {

    @OptIn(PublicPreviewAPI::class)
    @Provides
    @Singleton
    fun provideImagenModel(): ImagenModel {
        return ImagenModelConfiguration.model
    }

    @Provides
    @Singleton
    fun provideSmurfRemoteDataSource(
        imagenModel: ImagenModel
    ): SmurfRemoteDataSource {
        return SmurfRemoteDataSource(imagenModel)
    }
}