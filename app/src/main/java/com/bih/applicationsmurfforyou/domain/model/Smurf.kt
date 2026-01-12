package com.bih.applicationsmurfforyou.domain.model

import com.google.firebase.database.IgnoreExtraProperties

// Add a no-argument constructor and default values for Firebase deserialization
@IgnoreExtraProperties
data class Smurf(
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null // This MUST match the field name in your Firebase database
)
