package com.bih.applicationsmurfforyou.presentation.smurfify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.util.ConnectivityObserver
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

sealed class SmurfifyEvent {
    object ShowAd : SmurfifyEvent()
}

@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfifyUiState>(
        if (connectivityObserver.isNetworkAvailable()) SmurfifyUiState.Idle
        else SmurfifyUiState.Error("No internet connection.")
    )
    val uiState: StateFlow<SmurfifyUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SmurfifyEvent>()
    val eventFlow: SharedFlow<SmurfifyEvent> = _eventFlow.asSharedFlow()

    private var chosenImageUri: Uri? = null

    private val _networkStatus = MutableStateFlow(
        if (connectivityObserver.isNetworkAvailable()) ConnectivityObserver.Status.Available
        else ConnectivityObserver.Status.Unavailable
    )
    val networkStatus: StateFlow<ConnectivityObserver.Status> = _networkStatus.asStateFlow()

    init {
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        connectivityObserver.observe().onEach { status ->
            val oldStatus = _networkStatus.value
            _networkStatus.value = status
            if (status == ConnectivityObserver.Status.Available) {
                if (oldStatus != ConnectivityObserver.Status.Available) {
                    _uiState.value = SmurfifyUiState.Idle
                }
            } else {
                _uiState.value = SmurfifyUiState.Error("No internet connection.")
            }
        }.launchIn(viewModelScope)
    }

    fun onImageChosen(uri: Uri) {
        if (uri == Uri.EMPTY) {
            _uiState.value = SmurfifyUiState.Idle
            return
        }
        chosenImageUri = uri
        if (_networkStatus.value != ConnectivityObserver.Status.Available) {
            _uiState.value = SmurfifyUiState.Error("No internet connection. Please check your network settings.")
            return
        }

        processSmurfImage()
        viewModelScope.launch {
            _eventFlow.emit(SmurfifyEvent.ShowAd)
        }
    }

    fun processSmurfImage() {
        val uri = chosenImageUri ?: return

        if (_networkStatus.value != ConnectivityObserver.Status.Available) {
            _uiState.value = SmurfifyUiState.Error("No internet connection to smurfify the image.")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading
                val originalBitmap = uri.toBitmap(context)
                val scaledBitmap = originalBitmap.scale(1024)
                val img = editImage(scaledBitmap)
                _uiState.value = SmurfifyUiState.Success(img)

            } catch (e: Exception) {
                Log.e("SmurfViewModel", "Smurfify error", e)
                _uiState.value = SmurfifyUiState.Error(e.message.toString())
            }
        }
    }

    private suspend fun editImage(bitmap: Bitmap): Bitmap? {
        val model = Firebase.ai().generativeModel(
            modelName = "gemini-1.5-flash-preview-0514",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )

        val prompt = content {
            image(bitmap)
            text("Edit this image to make it look like a smurf cartoon, Blue skin, Smurf hat, white trousers, style of Peyo")
        }

        val response = model.generateContent(prompt)
        return response.candidates.first().content.parts.filterIsInstance<ImagePart>().firstOrNull()?.image
    }

    private fun Uri.toBitmap(context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        }
    }

    private fun Bitmap.scale(maxSize: Int): Bitmap {
        val originalWidth = this.width
        val originalHeight = this.height
        if (originalWidth <= maxSize && originalHeight <= maxSize) {
            return this
        }

        val scaleFactor = min(maxSize.toFloat() / originalWidth, maxSize.toFloat() / originalHeight)
        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
    }
}
