package com.bih.applicationsmurfforyou.presentation.explore

import com.bih.applicationsmurfforyou.domain.model.Smurf

sealed interface SmurfUiState {
    data class Success(val smurfs: List<Smurf>) : SmurfUiState
    data class Error(val message: String) : SmurfUiState
    object Loading : SmurfUiState
}
