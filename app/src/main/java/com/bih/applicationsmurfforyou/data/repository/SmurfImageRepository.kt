package com.bih.applicationsmurfforyou.data.repository


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.bih.applicationsmurfforyou.data.remote.ImageGenerationRequest
import com.bih.applicationsmurfforyou.data.remote.OpenAiImageApi
import com.bih.applicationsmurfforyou.domain.model.SmurfImage
import com.bih.applicationsmurfforyou.domain.util.Result
import javax.inject.Inject
import javax.inject.Named

class SmurfImageRepository @Inject constructor(
    private val api: OpenAiImageApi,
    @Named("openaiApiKey") private val apiKey: String
) {

    /**
     * Generates a Smurf-style image based on a text description.
     *
     * NOTE: This version does NOT actually use the user's photo, only text.
     * To truly edit/transform an input photo, you'd need the image-edit endpoint with multipart/form-data.
     */
    suspend fun generateSmurfFromDescription(description: String): Result<SmurfImage> {
        val fullPrompt = """
            Create a high-quality, fun cartoon Smurf character.
            Style: classic Smurfs (blue skin, white hat, white pants), friendly, kid-safe.
            Description of the person: $description
        """.trimIndent()

        val request = ImageGenerationRequest(
            prompt = fullPrompt,
            size = "1024x1024",
            responseFormat = "b64_json"
        )

        return try {
            val response = api.generateImage(
              //  authHeader = "Bearer $apiKey",
                request = request
            )

            val b64 = response.data.firstOrNull()?.b64Json
                ?: return Result.Error("No image data returned from OpenAI")

            val bitmap = decodeBase64ToBitmap(b64)
                ?: return Result.Error("Failed to decode image")

            Result.Success(SmurfImage(bitmap))
        } catch (t: Throwable) {
            Result.Error("OpenAI image generation failed: ${t.message}", t)
        }
    }

    private fun decodeBase64ToBitmap(b64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(b64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}