package com.bih.applicationsmurfforyou.data.remote.openai

import com.google.gson.annotations.SerializedName

// -------- REQUEST --------

data class OpenAIRequest(
    val model: String,
    val input: List<OpenAIInputItem>
)

data class OpenAIInputItem(
    val role: String,
    val content: List<OpenAIContent>
)

/**
 * Generic content item.
 * For text:  type = "input_text",  text != null
 * For image: type = "input_image", imageUrl != null
 */
data class OpenAIContent(
    val type: String,
    val text: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)

// -------- RESPONSE (simplified for text output) --------

data class OpenAIResponse(
    val id: String?,
    val model: String?,
    val output: List<OpenAIOutputItem>?
)

data class OpenAIOutputItem(
    val id: String?,
    val type: String?,   // e.g. "output_text"
    val role: String?,   // e.g. "assistant"
    val content: List<OpenAIOutputContent>?
)

data class OpenAIOutputContent(
    val type: String?,   // e.g. "output_text"
    val text: OpenAIOutputText?
)

data class OpenAIOutputText(
    val value: String?
)