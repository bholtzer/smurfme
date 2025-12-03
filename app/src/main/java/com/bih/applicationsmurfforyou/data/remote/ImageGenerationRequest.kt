package com.bih.applicationsmurfforyou.data.remote

import com.google.gson.annotations.SerializedName

data class ImageGenerationRequest(
    val model: String = "gpt-image-1",
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024",
    @SerializedName("response_format")
    val responseFormat: String = "b64_json" // so we get base64 back
)