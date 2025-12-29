package com.bih.applicationsmurfforyou.domain.repository

import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.bih.applicationsmurfforyou.data.repository.SmurfLocalDataSource
import com.bih.applicationsmurfforyou.data.repository.SmurfRemoteDataSource
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Domain repository interface.
 */
class SmurfRepository(
    private val local: SmurfLocalDataSource,
    private val remote: SmurfRemoteDataSource,
    private val firebaseStorage: FirebaseStorage
) {

    suspend fun getAllSmurfsCached(): List<Smurf> {
        val cached = local.getAllSmurfs()
        if (cached.isNotEmpty()) return cached

        // optional: fetch from cloud
        return cached
    }

    suspend fun createSmurf(sourceBitmap: Bitmap): Smurf {
        val generated = remote.generateSmurf(sourceBitmap)
        return local.saveSmurf(generated)
    }

    suspend fun getAllSmurfs(): List<Smurf> {
        val smurfImagesRef = firebaseStorage.reference.child("smurfs")
        Log.d("SmurfRepository", "START getAllSmurfs:" + SystemClock.currentThreadTimeMillis())
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
        Log.d("SmurfRepository", "END getAllSmurfs:" + SystemClock.currentThreadTimeMillis())
    }
}


