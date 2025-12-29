package com.bih.applicationsmurfforyou.domain.util


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bih.applicationsmurfforyou.data.ai.ImagenModelConfiguration.model
import com.google.firebase.ai.type.ImagenInlineImage
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.asImageOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL


object ImageDownloader {

    private val client = OkHttpClient()

    suspend fun downloadImage(url: String): ByteArray = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Download failed: ${response.code}")
            }

            val body = response.body ?: throw Exception("Empty body")
            body.bytes()
        }
    }

    suspend fun downloadImageBytes(url: String): ByteArray =
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { it.readBytes() }
        }

    fun bytesToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


    suspend fun loadBitmapFromUrl(url: String): Bitmap {
        val bytes = withContext(Dispatchers.IO) {
            URL(url).openStream().use { it.readBytes() }
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}