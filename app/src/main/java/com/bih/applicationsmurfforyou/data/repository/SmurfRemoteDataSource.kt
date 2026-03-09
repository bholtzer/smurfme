package com.bih.applicationsmurfforyou.data.repository

import android.graphics.Bitmap
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.type.ImagenForegroundMask
import com.google.firebase.ai.type.ImagenRawImage
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.toImagenInlineImage

class SmurfRemoteDataSource(
    private val imagenModel: ImagenModel
) {

    @OptIn(PublicPreviewAPI::class)
    suspend fun generateSmurf(
        sourceBitmap: Bitmap,
        prompt: String
    ): Bitmap {
        val response = imagenModel.editImage(
            prompt = prompt,
            referenceImages = listOf(
                ImagenRawImage(sourceBitmap.toImagenInlineImage()),
                ImagenForegroundMask()
            )
        )

        return response.images.first().asBitmap()
    }
}