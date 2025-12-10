package com.bih.applicationsmurfforyou.presentation.smurfify


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.ai.ImagenModelConfiguration
 import com.bih.applicationsmurfforyou.data.repository.ImagenRepository
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

                val model = ImagenModelConfiguration.model

                val imageResponse = model.generateImages(
                    prompt =  if(text.isEmpty())
                         "A hyper realistic picture of a t-rex with a blue bagpack in a prehistoric forest"
                    else text,

                )

                val image = imageResponse.images.first()
                val bitmapImage = image.asBitmap()

                _uiState.value = SmurfifyUiState.Success(bitmapImage)

            } catch (e: Exception) {
                Log.e("SmurfifyViewModel", "generateSmurf error: ${e.message}")
                _uiState.value = SmurfifyUiState.Error(e.message.toString())

            }
        }
    }


}