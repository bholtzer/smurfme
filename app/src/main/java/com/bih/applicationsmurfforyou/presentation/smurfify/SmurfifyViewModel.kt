package com.bih.applicationsmurfforyou.presentation.smurfify


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.usecase.GenerateSmurfImageUseCase
import com.bih.applicationsmurfforyou.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    private val generateSmurfImage: GenerateSmurfImageUseCase
) : ViewModel() {

    var uiState: SmurfifyUiState by mutableStateOf(SmurfifyUiState.Idle)
        private set

    fun createSmurf(description: String) {
        if (description.isBlank()) return

        viewModelScope.launch {
            uiState = SmurfifyUiState.Loading
            when (val result = generateSmurfImage(description)) {
                is Result.Success -> {
                    uiState = SmurfifyUiState.Success(result.data.bitmap)
                }
                is Result.Error -> {
                    uiState = SmurfifyUiState.Error(result.message)
                }
            }
        }
    }

    fun reset() {
        uiState = SmurfifyUiState.Idle
    }
}