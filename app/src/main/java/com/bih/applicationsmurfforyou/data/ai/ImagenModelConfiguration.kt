package com.bih.applicationsmurfforyou.data.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenGenerationConfig
import com.google.firebase.ai.type.ImagenImageFormat
import com.google.firebase.ai.type.ImagenPersonFilterLevel
import com.google.firebase.ai.type.ImagenSafetyFilterLevel
import com.google.firebase.ai.type.ImagenSafetySettings


import com.google.firebase.ai.type.PublicPreviewAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal object ImagenModelConfiguration {
    // [START android_imagen_model_configuration]
    @OptIn(PublicPreviewAPI::class)
    val config = ImagenGenerationConfig(
        numberOfImages = 1,
        aspectRatio = ImagenAspectRatio.SQUARE_1x1,
        imageFormat = ImagenImageFormat.jpeg(compressionQuality = 100),
        )

    // Initialize the Gemini Developer API backend service
    // For Vertex AI use Firebase.ai(backend = GenerativeBackend.vertexAI())
    @OptIn(PublicPreviewAPI::class)
    val model = Firebase.ai(backend = GenerativeBackend.googleAI()).imagenModel(
        modelName = "imagen-4.0-generate-001",

        generationConfig = config,
        safetySettings = ImagenSafetySettings(
            safetyFilterLevel = ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE,
            personFilterLevel = ImagenPersonFilterLevel.ALLOW_ALL
        ),
    )
    // [END android_imagen_model_configuration]

}
