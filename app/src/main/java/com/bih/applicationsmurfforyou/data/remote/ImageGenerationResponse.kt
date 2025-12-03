package com.bih.applicationsmurfforyou.data.remote

import com.google.gson.annotations.SerializedName

data class ImageGenerationResponse(
    val created: Long,
    val data: List<GeneratedImageData>
)

data class GeneratedImageData(
    // Present when response_format = "b64_json"
    @SerializedName("b64_json")
    val b64Json: String? = null,

    // Present when response_format = "url"
    val url: String? = null
)