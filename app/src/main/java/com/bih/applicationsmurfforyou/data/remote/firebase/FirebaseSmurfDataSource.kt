package com.bih.applicationsmurfforyou.data.remote.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Data source responsible for uploading images to Firebase Storage.
 */
class FirebaseStorageDataSource(
    private val context: Context,
    private val storage: FirebaseStorage
) {

    /**
     * Uploads the image pointed by [uri] to Firebase Storage
     * and returns its public download URL.
     */
    suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val fileName = "uploads/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream from URI")

        try {
            storageRef.putStream(inputStream).await()
        } finally {
            inputStream.close()
        }

        return@withContext storageRef.downloadUrl.await().toString()
    }
}