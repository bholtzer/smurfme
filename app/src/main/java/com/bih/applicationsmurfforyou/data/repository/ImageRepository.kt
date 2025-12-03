package com.bih.applicationsmurfforyou.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.bih.applicationsmurfforyou.data.api.ImageContent
import com.bih.applicationsmurfforyou.data.api.InputItem
import com.bih.applicationsmurfforyou.data.api.OpenAiApi
import com.bih.applicationsmurfforyou.data.api.TextContent
import com.bih.applicationsmurfforyou.data.api.VisionRequest
import com.bih.applicationsmurfforyou.data.remote.openai.OpenAiClient
import com.bih.applicationsmurfforyou.domain.util.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject
import javax.inject.Named

class ImageRepository @Inject constructor(
    private val api: OpenAiApi,
    @Named("openaiApiKey") private val apiKey: String,
    private val client: OpenAiClient
) {

    private suspend fun downloadImage(url: String): File = withContext(Dispatchers.IO) {
        val tmp = File.createTempFile("smurf_input", ".jpg")

        URL(url).openStream().use { input ->
            tmp.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        tmp
    }


    // @OptIn(ExperimentalEncodingApi::class)
    suspend fun createSmurfImage(firebaseUrl: String): Result<Any> {
        return try {


            // Build a prompt referencing the Firebase image URL
            val prompt =
                "Transform the person in this photo into a Smurf. Blue skin, white hat, cute cartoon style. Image URL: $firebaseUrl"


            /*  val result = openAI.images.generate(
                    model = "gpt-image-1",
                    prompt = "Turn this person into a Smurf character",
                    size = "1024x1024",
                    image = firebaseUrl // <— optional, you can embed the URL
                )*/


            val request = VisionRequest(
                model = "gpt-4.1",

                input = listOf(
                    InputItem(
                        role = "user",
                        content = listOf(
                            TextContent(
                                type = "input_text",
                                text = "create this image to a Smurf character."
                            ),
                            ImageContent(
                                type = "input_image",
                                image_url = firebaseUrl
                            )
                        )
                    )
                )
            )


            val response = client.api.sendVisionRequest(
                authHeader = "Bearer $apiKey",
                request = request

            )

            Log.d("OPENAI", "Response:\n${Gson().toJson(response)}")

            response.error?.let {
                return Result.Error(it.message ?: "Unknown OpenAI error")
            }

            //  val smurfText = response.output
            val smurfText = response.output
                ?.firstOrNull()
                ?.content
                ?.firstOrNull { it.type == "output_text" }
                ?.text
            Log.d("OPENAI", "smurfText:\n${smurfText}")


            // Convert Base64 → Bitmap


            Result.Success(smurfText)


        } catch (e: Exception) {
            Log.e("ImageRepository", "Smurf generation error: ${e.message}")
            Result.Error("Failed to generate Smurf image", e)
        } as Result<Any>
    }

}