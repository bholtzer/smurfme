package com.bih.applicationsmurfforyou.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.math.max

object Utils {

    private var tts: TextToSpeech? = null

    fun speakText(context: Context, text: String) {
        if (tts == null) {
            tts = TextToSpeech(context) {
                if (it == TextToSpeech.SUCCESS) {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun downloadImageToFile(context: Context, image: String): File {
        val file = File(context.cacheDir, "input_image.png")

        if (image.startsWith("content://")) {
            // Handle content:// URIs
            val uri = image.toUri()
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Failed to open content URI")
        } else if (image.startsWith("file://")) {
            // Handle local file:// URIs
            val uri = image.toUri()
            File(uri.path!!).copyTo(file, overwrite = true)
        } else {
            // Handle HTTP/HTTPS
            val request = Request.Builder().url(image).build()
            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Failed to download image")
                response.body?.byteStream()?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }

        return file
    }

    fun uriToTempFile(context: Context, uri: Uri): File {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream for $uri")
        val outFile = File.createTempFile("input_", ".jpg", context.cacheDir)
        outFile.outputStream().use { output -> input.copyTo(output) }
        return outFile
    }

    fun downloadImageToFileOld(context: Context, image: String): File {
        val file = File(context.cacheDir, "input_image.png")
        val request = Request.Builder().url(image).build()
        OkHttpClient().newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Failed to download image")
            file.outputStream().use { output ->
                response.body?.byteStream()?.copyTo(output)
            }
        }
        return file
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun downscale(context: Context, uri: Uri, maxDim: Int = 1280): File {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        val ratio = max(bitmap.width, bitmap.height).toFloat() / maxDim
        val out = if (ratio > 1f) {
            Bitmap.createScaledBitmap(bitmap, (bitmap.width/ratio).toInt(), (bitmap.height/ratio).toInt(), true)
        } else bitmap
        val file = File.createTempFile("scaled_", ".jpg", context.cacheDir)
        file.outputStream().use { out.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return file
    }

     suspend fun uploadToFirebase(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val storage = FirebaseStorage.getInstance().reference
            val fileName = "uploads/${UUID.randomUUID()}.jpg"
            val fileRef = storage.child(fileName)

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Cannot open input stream")

            fileRef.putStream(inputStream).await()
            inputStream.close()

            return@withContext fileRef.downloadUrl.await().toString()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}