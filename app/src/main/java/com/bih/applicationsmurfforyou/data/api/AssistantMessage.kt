package com.bih.applicationsmurfforyou.data.api

sealed class AssistantContent {
    data class OutputImageInstruction(
        val type: String = "output_image"
    ) : AssistantContent()
}