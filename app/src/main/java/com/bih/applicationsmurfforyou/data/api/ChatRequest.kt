package com.bih.applicationsmurfforyou.data.api


data class ImageContent(
    val type: String = "input_image",
    val image_url: String
)

data class InputItem(
    val role: String = "user",
    val content: List<Any>
)

data class TextContent(
    val type: String = "input_text",
    val text: String
)

data class OpenAIFormat(
    val prompt: String,
    val image : String
)


