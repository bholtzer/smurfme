package com.bih.applicationsmurfforyou.data.repository

import android.util.Log
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class SmurfRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage

){//} : SmurfRepository {



  /*  suspend fun getAllSmurfsCached(): List<Smurf> {
        val cached = localDataSource.getSmurfs()
        if (cached.isNotEmpty()) return cached

        val remote = remoteDataSource.fetchSmurfs()
        localDataSource.saveSmurfs(remote)
        return remote
    }*/

    suspend fun getAllSmurfs(): List<Smurf> {
        val smurfImagesRef = firebaseStorage.reference.child("smurfs")

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

    suspend fun smurfImage(imageUrl: String): String {
        TODO("Not yet implemented")
    }


}