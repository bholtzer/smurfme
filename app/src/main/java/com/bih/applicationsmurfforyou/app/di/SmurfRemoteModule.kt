package com.bih.applicationsmurfforyou.app.di

import com.bih.applicationsmurfforyou.data.repository.SmurfRemoteDataSource
import com.google.firebase.Firebase
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmurfRemoteModule {

    @Provides
    @Singleton
    fun provideImagenModel(): ImagenModel {
        return Firebase.ai(
            backend = GenerativeBackend.vertexAI("us-central1")
        ).imagenModel("imagen-3.0-capability-001")
    }

   /* @Provides
    @Singleton
    fun provideSmurfRemoteDataSource(
        imagenModel: ImagenModel
    ): SmurfRemoteDataSource {
        return SmurfRemoteDataSource(imagenModel)
    }*/
}