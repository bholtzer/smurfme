package com.bih.applicationsmurfforyou.presentation.smurfify

sealed class SmurfUiState {
    object Idle : SmurfUiState()
    object Loading : SmurfUiState()
    data class Success(val description: String) : SmurfUiState()
    data class Error(val message: String) : SmurfUiState()
}