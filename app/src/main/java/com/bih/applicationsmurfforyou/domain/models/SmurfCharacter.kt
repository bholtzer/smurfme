package com.bih.applicationsmurfforyou.domain.models

import com.google.firebase.database.IgnoreExtraProperties

// Add a no-argument constructor and default values for Firebase deserialization
@IgnoreExtraProperties
data class SmurfCharacter(
    val name: String? = null,
    val imageUrl: String? = null
)
