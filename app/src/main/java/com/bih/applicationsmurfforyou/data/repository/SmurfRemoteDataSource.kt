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
            A whimsical, high-quality 3D animation style Smurf character. 
            Transform the person in the image into a Smurf while preserving their unique facial features, expression, and distinct characteristics (like glasses, hair style, or facial hair). 
            The character should have iconic blue skin, be wearing a classic white Phrygian-style Smurf hat and white trousers. 
            The style should be consistent with modern animated feature films, with soft lighting and vibrant colors.
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