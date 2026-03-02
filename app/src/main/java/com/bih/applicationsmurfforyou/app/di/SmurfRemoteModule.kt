package com.bih.applicationsmurfforyou.app.di

import com.bih.applicationsmurfforyou.data.repository.SmurfRemoteDataSource
import com.google.firebase.Firebase
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenPersonFilterLevel
import com.google.firebase.ai.type.ImagenSafetyFilterLevel
import com.google.firebase.ai.type.ImagenSafetySettings
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
        ).imagenModel(
            modelName = "imagen-3.0-capability-001",
            safetySettings = ImagenSafetySettings(
                safetyFilterLevel = ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE,
                personFilterLevel = ImagenPersonFilterLevel.ALLOW_ALL
            )
        )
    }

    @Provides
    @Singleton
    fun provideSmurfRemoteDataSource(
        imagenModel: ImagenModel
    ): SmurfRemoteDataSource {
        return SmurfRemoteDataSource(imagenModel)
    }
}