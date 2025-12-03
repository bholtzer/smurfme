package com.bih.applicationsmurfforyou.data.api

data class UserInput(
    val role: String = "user",
    val content: List<UserContent>
)
