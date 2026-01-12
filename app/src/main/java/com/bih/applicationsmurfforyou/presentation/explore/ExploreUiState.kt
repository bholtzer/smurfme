package com.bih.applicationsmurfforyou.presentation.explore

import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.models.SmurfCharacter

sealed class ExploreUiState {
    object Idle : ExploreUiState()
    object Loading : ExploreUiState()
    data class Loaded(
        val smurfs: List<Smurf>,
        val isRefreshing: Boolean = false
    ) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}
