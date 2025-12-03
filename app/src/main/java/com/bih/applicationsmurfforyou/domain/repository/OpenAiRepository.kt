package com.bih.applicationsmurfforyou.domain.repository

import android.util.Log
import com.bih.applicationsmurfforyou.data.api.ImageContent
import com.bih.applicationsmurfforyou.data.api.InputItem
import com.bih.applicationsmurfforyou.data.api.OpenAiApi
import com.bih.applicationsmurfforyou.data.api.TextContent
import com.bih.applicationsmurfforyou.data.api.VisionRequest
import com.bih.applicationsmurfforyou.domain.util.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named


class   OpenAiRepository @Inject constructor(
    private val api: OpenAiApi,
    @Named("openaiApiKey") private val apiKey: String
) {

    suspend fun smurfifyImage(
        imageUrl: String
    ): Result<String?> = withContext(Dispatchers.IO) {

        Log.d("OPENAI", "Sending Vision request...")
        Log.d("OPENAI","imageUrl $imageUrl")
        try {
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
                            image_url = imageUrl
                        )
                    )
                )
            )
        )

            val response = api.sendVisionRequest(
                authHeader = "Bearer $apiKey",
                request = request
            )


            Log.d("OPENAI", "Response:\n${Gson().toJson(response)}")

            response.error?.let {
                return@withContext Result.Error(it.message ?: "Unknown OpenAI error")
            }

          //  val smurfText = response.output
            val smurfText = response.output
                ?.firstOrNull()
                ?.content
                ?.firstOrNull { it.type == "output_text" }
                ?.text
            Log.d("OPENAI", "smurfText:\n${smurfText}")

            Result.Success(smurfText)

        } catch (e: retrofit2.HttpException) {
            Log.e("OPENAI", "HTTP error ${e.code()}: ${e.message()}")
            Result.Error("HTTP ${e.code()}: ${e.message()}", e)

        } catch (e: Exception) {
            Log.e("OPENAI", "General error: ${e.message}")
            Result.Error("Smurfify failed: ${e.message}", e)
        }
    }
}
