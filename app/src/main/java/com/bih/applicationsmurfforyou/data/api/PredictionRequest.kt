package com.bih.applicationsmurfforyou.data.api

data class PredictionRequest(
    val version: String,
    val input: Map<String, String>
)