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
        sourceBitmap: Bitmap
    ): Bitmap {
        val prompt = """
            A photorealistic, high-detail smurf version of the person in this image. 
            Preserve the facial features, expression, and any accessories like glasses or beards. 
            The skin must be blue and they must be wearing a white smurf hat.
        """.trimIndent()

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