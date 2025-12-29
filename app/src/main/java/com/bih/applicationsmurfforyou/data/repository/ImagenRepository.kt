package com.bih.applicationsmurfforyou.data.repository

import android.graphics.Bitmap
import com.bih.applicationsmurfforyou.data.ai.ImagenModelConfiguration
import com.google.firebase.ai.type.PublicPreviewAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagenRepository @Inject constructor() {

    @OptIn(PublicPreviewAPI::class)
    suspend fun generateImage(prompt: String): Bitmap {
        val model = ImagenModelConfiguration.model
        val response = model.generateImages(prompt)
        return response.images.first().asBitmap()
    }

}