package com.bih.applicationsmurfforyou.data.repository

import android.R.attr.prompt
import android.util.Log
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.mediapipe.framework.MediaPipeException
import com.google.mediapipe.tasks.vision.imagegenerator.ImageGenerator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class SmurfRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage

) : SmurfRepository {

    override suspend fun getAllSmurfs(): List<Smurf> {
        val smurfImagesRef = firebaseStorage.reference.child("smurfs")

        // Prepare input
     //   val mpImage = ImageGenerator.createImage(inputBitmap) // Or use a plugin for condition image
    /*    val generationOptions = ImageGenerator.ImageGeneratorOptions.builder()
            .setPrompt(prompt)
            // Add conditional inputs if using a specific img2img model/plugin
            .build()*/

// Generate the new image
    //    val result = imageGenerator.generate(mpImage, generationOptions)

// Get the output bitmap from the result
      //  val generatedBitmap = result.generatedImage().bitmap()

        return try {
            val result = smurfImagesRef.listAll().await()
            result.items.map { imageRef ->

                // Firebase image filename (ex: “papa.png”)
                val fileName = imageRef.name

                // Full download URL (ex: https://firebasestorage...)
                val downloadUrl = imageRef.downloadUrl.await().toString()

                Smurf(
                    name = fileName.substringBeforeLast("."),     // "papa"
                    description = "A classic smurf named $fileName", // 🔹 Placeholder description
                    image = downloadUrl                            // 🔹 match `image: String`
                )
            }
        } catch (e: Exception) {
            Log.e("SmurfRepositoryImpl", "getAllSmurfs error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun smurfImage(imageUrl: String): String {
        TODO("Not yet implemented")
    }


}