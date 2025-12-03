package com.bih.applicationsmurfforyou.data.api

data class ResponsesResponse(
    val id: String,
    val model: String,
    val output: List<OutputBlock>
)