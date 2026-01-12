package com.bih.applicationsmurfforyou.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.bih.applicationsmurfforyou.domain.model.Smurf
import java.io.File
import java.util.UUID

class SmurfLocalDataSource(
    private val context: Context
) {
    private val smurfsDir = File(context.cacheDir, "smurfs")

    init {
        if (!smurfsDir.exists()) smurfsDir.mkdirs()
    }

    fun getAllSmurfs(): List<Smurf> {
        return smurfsDir
            .listFiles()
            ?.filter { it.extension == "jpg" }
            ?.map {
                Smurf(
                    name = it.nameWithoutExtension,
                    description = "A classic smurf named ${it.nameWithoutExtension}",
                    imageUrl = it.toString()
                )
            }
            ?: emptyList()
    }

    fun saveSmurf(bitmap: Bitmap): Smurf {
        val id = UUID.randomUUID().toString()
        val file = File(smurfsDir, "$id.jpg")

        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
        }

        return Smurf(file.name, "A classic smurf named ${file.nameWithoutExtension}", bitmap.toString())
    }

    fun clear() {
        smurfsDir.deleteRecursively()
        smurfsDir.mkdirs()
    }
}