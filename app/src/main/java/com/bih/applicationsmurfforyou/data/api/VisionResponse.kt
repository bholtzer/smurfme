package com.bih.applicationsmurfforyou.data.api



data class VisionResponse(
    val id: String?,
    val output: List<OutputMessage>?,
    val status: String?,
    val error: ErrorBody?
)

data class OutputMessage(
    val type: String?,
    val content: List<OutputContent>?
)

data class OutputContent(
    val type: String?,
    val image: OutputImage? = null,
    val text: String?
)

data class OutputImage(
    val b64_json: String,
    val format: String? = null
)

data class OutputItem(
    val type: String?,
    val output_text: String?
)

data class ResponseFormat(
    val type: String = "text"
)

data class ErrorBody(
    val message: String?,
    val type: String?,
    val code: String?
)