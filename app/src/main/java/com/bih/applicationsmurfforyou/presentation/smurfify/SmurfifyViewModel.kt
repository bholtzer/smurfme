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
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SmurfifyEvent {
    object ShowAd : SmurfifyEvent()
}

@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfifyUiState>(SmurfifyUiState.Idle)
    var uiState: StateFlow<SmurfifyUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SmurfifyEvent>()
    val eventFlow: SharedFlow<SmurfifyEvent> = _eventFlow.asSharedFlow()

    fun onImageChosen(uri: Uri) {
        viewModelScope.launch {
            // Immediately request to show an ad
            _eventFlow.emit(SmurfifyEvent.ShowAd)

            // Start the AI image generation in the background
            launch {
                try {
                    _uiState.value = SmurfifyUiState.Loading

                    val bitmap = uri.toBitmap(context)

                    val model = Firebase.ai(
                        backend = GenerativeBackend.googleAI()
                    ).generativeModel(
                        modelName = "gemini-1.5-flash"
                    )

                    val prompt = content {
                        image(bitmap)
                        text(
                            """
                            Your main goal is to transform the person in the image into a **cute, 3D animated Smurf character** while preserving their recognizable facial features.

                            --- MANDATORY RULES ---
                            1.  **CUTE CARICATURE OF THE FACE:** The Smurf's face **MUST** be a *cute caricature* of the person in the image. It is critical to keep their unique facial structure (mouth, nose shape, smile) so they are still recognizable. **DO NOT** use a generic Smurf face. The goal is to see *their* face, but in a cute, cartoon Smurf style.
                            2.  **ICONIC SMURF HAT:** The character **MUST** be wearing the classic large, floppy, white Smurf hat.

                            --- STYLE & APPEARANCE ---
                            - **Overall Style:** Modern 3D animation (like Pixar or Dreamworks), with a focus on being cute and appealing.
                            - **Eyes:** Transform the eyes to be large, white, and expressive in a classic cartoon style, fitting the person's original eye shape and position.
                            - **Skin:** The skin must be smooth, solid blue, with no human textures.
                            - **Background:** Do not change the original background.
                            """
                        )
                    }

                    val response = model.generateContent(prompt)
                    val imageBitmap = response.candidates
                        .firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstNotNullOfOrNull { it.asImageOrNull() }
                        ?: throw IllegalStateException("No image returned from model")

                    _uiState.value = SmurfifyUiState.Success(imageBitmap)

                } catch (e: Exception) {
                    Log.e("SmurfifyViewModel", "Smurfify error: ${e.message}")
                    _uiState.value = SmurfifyUiState.Error(e.message.toString())
                }
            }
        }
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
