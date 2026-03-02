package com.bih.applicationsmurfforyou.presentation.smurfify


import android.graphics.Bitmap
import android.net.Uri

sealed interface SmurfifyUiState {
    object Idle : SmurfifyUiState
    object Loading : SmurfifyUiState
    data class Success(val bitmap: Bitmap?, val imageUri: Uri?) : SmurfifyUiState
    data class Error(val message: String) : SmurfifyUiState
}