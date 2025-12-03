package com.bih.applicationsmurfforyou.data.api

data class ImageEditResponse(
    val created: Long,
    val data: List<ImageData>
)

data class ImageData(
    val b64_json: String
)