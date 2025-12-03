package com.bih.applicationsmurfforyou.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApi {

    @POST("v1/responses")
    suspend fun sendVisionRequest(
        @Header("Authorization") authHeader: String,
        @Body request: VisionRequest
    ): VisionResponse

}

