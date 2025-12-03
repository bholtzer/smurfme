package com.bih.applicationsmurfforyou.app.di

import com.bih.applicationsmurfforyou.BuildConfig
import com.bih.applicationsmurfforyou.data.api.OpenAiApi
import com.bih.applicationsmurfforyou.data.remote.OpenAiImageApi
import com.bih.applicationsmurfforyou.data.remote.openai.OpenAiClient
import com.bih.applicationsmurfforyou.data.repository.SmurfImageRepository
import com.bih.applicationsmurfforyou.domain.usecase.GenerateSmurfImageUseCase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenAiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            // add logging interceptor here if you want
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideOpenAiImageApi(retrofit: Retrofit): OpenAiImageApi =
        retrofit.create(OpenAiImageApi::class.java)

    @Provides
    @Singleton
    fun provideOpenAiApi(retrofit: Retrofit): OpenAiApi =
        retrofit.create(OpenAiApi::class.java)

    @Provides
    @Singleton
    fun provideOpenAiClient(
        @Named("openaiApiKey") apiKey: String
    ): OpenAiClient {
        return OpenAiClient(apiKey)
    }

    @Provides
    @Singleton
    @Named("openaiApiKey")
    fun provideOpenAiApiKey(): String = BuildConfig.OPENAI_API_KEY

    @Provides
    @Singleton
    fun provideSmurfImageRepository(
        api: OpenAiImageApi,
        @Named("openaiApiKey") apiKey: String
    ): SmurfImageRepository = SmurfImageRepository(api, apiKey)

    @Provides
    @Singleton
    fun provideGenerateSmurfImageUseCase(
        repo: SmurfImageRepository
    ): GenerateSmurfImageUseCase = GenerateSmurfImageUseCase(repo)
}