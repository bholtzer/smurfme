package com.bih.applicationsmurfforyou.data.repository

import android.content.Context
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.google.firebase.ai.ImagenModel
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmurfModule {

    @Provides
    @Singleton
    fun provideLocalDataSource(
        @ApplicationContext context: Context
    ): SmurfLocalDataSource {
        return SmurfLocalDataSource(context)
    }

   /* @Provides
    @Singleton
    fun provideRepository(
        localDataSource: SmurfLocalDataSource
    ): SmurfRepository {
        return SmurfRepository(localDataSource)
    }*/

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        imagenModel: ImagenModel
    ): SmurfRemoteDataSource {
        return SmurfRemoteDataSource(imagenModel)
    }

    // Repository (LOCAL + REMOTE)
    @Provides
    @Singleton
    fun provideRepository(
        localDataSource: SmurfLocalDataSource,
        remoteDataSource: SmurfRemoteDataSource,
        firebaseStorage: FirebaseStorage
    ): SmurfRepository {
        return SmurfRepository(
            local = localDataSource,
            remote = remoteDataSource,
            firebaseStorage = firebaseStorage
        )
    }
}