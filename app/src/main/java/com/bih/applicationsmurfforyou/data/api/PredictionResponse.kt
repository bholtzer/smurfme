package com.bih.applicationsmurfforyou.data.api

data class PredictionResponse(
    val id: String,
    val status: String,
    val output: List<String>?
)
