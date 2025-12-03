package com.bih.applicationsmurfforyou.presentation.smurfify


import android.graphics.Bitmap

sealed interface SmurfifyUiState {
    object Idle : SmurfifyUiState
    object Loading : SmurfifyUiState
    data class Success(val bitmap: Bitmap) : SmurfifyUiState
    data class Error(val message: String) : SmurfifyUiState
}