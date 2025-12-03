package com.bih.applicationsmurfforyou.data.remote.openai

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenAIService {

    private const val BASE_URL = "https://api.openai.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
               // .addHeader("Authorization", "Bearer YOUR_OPENAI_API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()



 }