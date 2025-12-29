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
            Transform the person in the image into a cartoon Smurf character.
            Preserve facial expression, identity, and head pose.
            Apply smooth blue cartoon skin and a classic white Smurf hat.
            Keep the same hairstyle and face shape.
            Cartoon illustration style with clean outlines and soft shading.
            Do not change the background.
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