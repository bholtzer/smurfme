package com.bih.applicationsmurfforyou.presentation.smurfify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenRawImage
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.toImagenInlineImage
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

sealed class SmurfifyEvent {
    object ShowAd : SmurfifyEvent()
}

@OptIn(PublicPreviewAPI::class)
@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Named("smurfGallery") private val smurfGalleryDir: File
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfifyUiState>(SmurfifyUiState.Idle)
    val uiState: StateFlow<SmurfifyUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SmurfifyEvent>()
    val eventFlow: SharedFlow<SmurfifyEvent> = _eventFlow.asSharedFlow()

    private val imagenModel = Firebase.ai(
        backend = GenerativeBackend.vertexAI("us-central1")
    ).imagenModel("imagen-3.0-capability-001")

    fun onImageChosen(uri: Uri) {
        if (uri == Uri.EMPTY) {
            _uiState.value = SmurfifyUiState.Idle
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading
                _eventFlow.emit(SmurfifyEvent.ShowAd)

                val bitmap = uri.toBitmap(context)


                val prompt = """Create a 3D animated movie-style image. The scene is a whimsical Smurf village with large, colorful mushroom houses under a bright, cheerful sky.
                
                In the center of the scene is a single Smurf character. This character is short, stout, has smooth, solid blue skin, and is wearing the classic Smurf outfit: a large, white, floppy hat and white trousers.
                
                **CRITICAL INSTRUCTION:**
                The face of this Smurf **MUST** be a recognizable, stylized caricature of the person from the reference image.
                - **Preserve:** The core structure of their smile, nose, and jawline.
                - **Adapt:** Their features into a cute, 3D animated style. Their eyes should be large and expressive but retain the original person's general shape and position.
                - **Do Not:** Simply paste the real face onto the Smurf body. It must be a cohesive, artistic transformation.
                
                The final image must feel like a real Smurf that just happens to look like the person in the photo.
                """



                val response = imagenModel.editImage(
                    prompt = prompt,
                    referenceImages = listOf(ImagenRawImage(bitmap.toImagenInlineImage()))
                )

                val imageBitmap = response.images.firstOrNull()?.asBitmap()
                    ?: throw IllegalStateException("No image returned from model")

                val imageUri = saveBitmap(imageBitmap)
                _uiState.value = SmurfifyUiState.Success(imageBitmap, imageUri)

            } catch (e: Exception) {
                Log.e("SmurfifyViewModel", "Smurfify error: ${e.message}")
                _uiState.value = SmurfifyUiState.Error(e.message.toString())
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
