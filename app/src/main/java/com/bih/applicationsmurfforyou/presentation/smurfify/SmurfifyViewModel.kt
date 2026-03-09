package com.bih.applicationsmurfforyou.presentation.smurfify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.R
import com.bih.applicationsmurfforyou.data.repository.SmurfRemoteDataSource
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named
import kotlin.random.Random

sealed class SmurfifyEvent {
    object ShowAd : SmurfifyEvent()
}

@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Named("smurfGallery") private val smurfGalleryDir: File,
    private val smurfRemoteDataSource: SmurfRemoteDataSource,
    private val analytics: FirebaseAnalytics
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfifyUiState>(SmurfifyUiState.Idle)
    val uiState: StateFlow<SmurfifyUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SmurfifyEvent>()
    val eventFlow: SharedFlow<SmurfifyEvent> = _eventFlow.asSharedFlow()

    private var lastChosenUri: Uri? = null
    
    private var processCount = 0
    private var trapThreshold = Random.nextInt(1, 11)

    init {
        logScreenView()
    }

    private fun logScreenView() {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Smurfify Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "SmurfifyViewModel")
        }
    }

    fun onImageChosen(uri: Uri) {
        if (uri == Uri.EMPTY) {
            _uiState.value = SmurfifyUiState.Idle
            return
        }
        lastChosenUri = uri
        analytics.logEvent("image_chosen") {
            param("source", if (uri.toString().contains("cache")) "camera" else "gallery")
        }
        processImage(uri)
    }

    fun onRefresh() {
        lastChosenUri?.let {
            analytics.logEvent("Smurfify_refresh", null)
            processImage(it)
        }
    }

    private fun processImage(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading
                _eventFlow.emit(SmurfifyEvent.ShowAd)

                processCount++
                
                val originalBitmap = uri.toBitmap(context)
                // Ensure the bitmap has even dimensions to avoid size mismatch with internal mask generation
                val bitmap = if (originalBitmap.width % 2 != 0 || originalBitmap.height % 2 != 0) {
                    val newWidth = originalBitmap.width - (originalBitmap.width % 2)
                    val newHeight = originalBitmap.height - (originalBitmap.height % 2)
                    Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                } else {
                    originalBitmap
                }

                val resultBitmap: Bitmap
                val isTrap = processCount >= trapThreshold
                
                if (isTrap) {
                    // Reset trap logic
                    processCount = 0
                    trapThreshold = Random.nextInt(1, 11)
                    
                    // Randomly choose between Gargamel and Azrael
                    val isGargamel = Random.nextBoolean()
                    val villainName = if (isGargamel) "Gargamel" else "Azrael"
                    
                    analytics.logEvent("Smurfify_trap_triggered") {
                        param("villain", villainName)
                    }

                    val trapPrompt = if (isGargamel) {
                        """
                            A 2D cartoon caricature in the classic 'ligne claire' style of Peyo. 
                            Transform the person in the image into Gargamel, the wicked wizard from the Smurfs. 
                            Features: a large hooked nose, a balding head with scruffy black hair on the sides, and a tattered black wizard robe with visible patches. 
                            Preserve the person's original facial expression, glasses, and recognizable features while adapting them to Gargamel's iconic look. 
                            Use bold black outlines, flat vibrant colors, and a clean hand-drawn comic book aesthetic.
                        """.trimIndent()
                    } else {
                        """
                            A 2D cartoon illustration in the classic 'ligne claire' style of Peyo. 
                            Transform the person in the image into Azrael, the scruffy orange tabby cat from the Smurfs. 
                            The cat should have a cunning, mischievous expression that mirrors the person's original face. 
                            Features: scruffy orange fur, a notched ear, and the person's recognizable traits (like glasses if present) translated onto the cat character. 
                            Use bold black outlines, vibrant flat colors, and a high-quality hand-drawn comic book style.
                        """.trimIndent()
                    }

                    resultBitmap = smurfRemoteDataSource.generateSmurf(bitmap, trapPrompt)
                } else {
                    val peyoSmurfPrompt = """
                        A 2D cartoon caricature in the classic 'ligne claire' style of Peyo. 
                        Transform the subject in the photo into a cute Smurf character with vibrant blue skin and a large white floppy Phrygian-style Smurf hat. 
                        Meticulously preserve the person's unique facial features, expression, and distinct characteristics (like glasses, hairstyle, or facial hair) so they are instantly recognizable. 
                        The character should be wearing classic white Smurf trousers. 
                        The final result must look like a clean, high-quality, vibrant comic book illustration with bold black outlines and simple, effective shading, staying perfectly true to the beloved aesthetic of the original Smurfs cartoons.
                    """.trimIndent()

                    resultBitmap = smurfRemoteDataSource.generateSmurf(bitmap, peyoSmurfPrompt)
                    analytics.logEvent("Smurfify_success", null)
                }

                val imageUri = saveBitmap(resultBitmap)
                _uiState.value = SmurfifyUiState.Success(resultBitmap, imageUri)

            } catch (e: Exception) {
                Log.e("SmurfifyViewModel", "Smurfify error: ${e.message}")
                _uiState.value = SmurfifyUiState.Error(e.message.toString())
                analytics.logEvent("Smurfify_error") {
                    param("error_msg", e.message ?: "unknown")
                }
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap): Uri {
        val file = File(smurfGalleryDir, "smurf_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    private fun Uri.toBitmap(context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        }
    }
}
