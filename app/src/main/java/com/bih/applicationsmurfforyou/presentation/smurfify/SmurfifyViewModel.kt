package com.bih.applicationsmurfforyou.presentation.smurfify


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.repository.ImagenRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    private val imagenRepository: ImagenRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfifyUiState>(SmurfifyUiState.Idle)
    var uiState: StateFlow<SmurfifyUiState> = _uiState.asStateFlow()

    fun generateSmurf(text: String) {
        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading

              //  val model = ImagenModelConfiguration.model
                val model = Firebase.ai(
                    backend = GenerativeBackend.googleAI()
                ).generativeModel(
                    modelName = "gemini-2.0-flash"
                )


                val prompt = content {
                    text(
                        """
                      Transform the person in the image into a cartoon Smurf character.
                    Preserve facial expression, identity, and head pose.
                    Apply smooth blue cartoon skin and a classic white Smurf hat.
                  Keep the same hairstyle and face shape.
                   Cartoon illustration style with clean outlines and soft shading.
                   Do not change the background.
                    """
                    )
                }

                val response = model.generateContent(prompt)
                val imageBitmap = response.candidates
                    .firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstNotNullOfOrNull { it.asImageOrNull() }
                    ?: throw IllegalStateException("No image returned")



               // val image = imageResponse.images.first()
               // val bitmapImage = image.asBitmap()

                _uiState.value = SmurfifyUiState.Success(imageBitmap)

            } catch (e: Exception) {
                Log.e("SmurfifyViewModel", "generateSmurf error: ${e.message}")
                _uiState.value = SmurfifyUiState.Error(e.message.toString())

            }
        }
    }


}