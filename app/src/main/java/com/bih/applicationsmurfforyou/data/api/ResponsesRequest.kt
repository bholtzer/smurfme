package com.bih.applicationsmurfforyou.data.api

data class ResponsesRequest(
    val model: String,
    val response_format: ResponseFormat,
    val input: List<UserInput>,
    val messages: List<AssistantMessage>
)

data class AssistantMessage(
    val role: String = "assistant",
    val content: List<AssistantContent>
)