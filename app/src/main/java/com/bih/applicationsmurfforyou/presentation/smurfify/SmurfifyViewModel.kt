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
            analytics.logEvent("smurfify_refresh", null)
            processImage(it)
        }
    }

    private fun processImage(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading
                _eventFlow.emit(SmurfifyEvent.ShowAd)

                processCount++
                
                val resultBitmap: Bitmap
                val isTrap = processCount >= trapThreshold
                
                if (isTrap) {
                    processCount = 0
                    trapThreshold = Random.nextInt(1, 11)
                    val trapResId = if (Random.nextBoolean()) R.drawable.gargamel else R.drawable.azrael
                    val villainName = if (trapResId == R.drawable.gargamel) "Gargamel" else "Azrael"
                    analytics.logEvent("smurfify_trap_triggered") {
                        param("villain", villainName)
                    }
                    resultBitmap = BitmapFactory.decodeResource(context.resources, trapResId)
                } else {
                    val bitmap = uri.toBitmap(context)
                    resultBitmap = smurfRemoteDataSource.generateSmurf(bitmap)
                    analytics.logEvent("smurfify_success", null)
                }

                val imageUri = saveBitmap(resultBitmap)
                _uiState.value = SmurfifyUiState.Success(resultBitmap, imageUri)

            } catch (e: Exception) {
                Log.e("SmurfifyViewModel", "Smurfify error: ${e.message}")
                _uiState.value = SmurfifyUiState.Error(e.message.toString())
                analytics.logEvent("smurfify_error") {
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
