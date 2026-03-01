package com.bih.applicationsmurfforyou.app.di

import android.app.Application
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
import java.io.File
import javax.inject.Named
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

    @Provides
    @Singleton
    @Named("smurfGallery")
    fun provideSmurfGalleryDirectory(app: Application): File {
        val directory = File(app.filesDir, "smurf_gallery")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }
}
