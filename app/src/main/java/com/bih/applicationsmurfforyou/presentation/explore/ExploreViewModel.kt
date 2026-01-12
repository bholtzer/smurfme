package com.bih.applicationsmurfforyou.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: SmurfRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Idle)
    val uiState: StateFlow<ExploreUiState> = _uiState

    init {
        loadSmurfs(isRefreshing = false)
    }

    fun onRefresh() {
        loadSmurfs(isRefreshing = true)
    }

    private fun loadSmurfs(isRefreshing: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (isRefreshing && currentState is ExploreUiState.Loaded) {
                _uiState.value = currentState.copy(isRefreshing = true)
            } else {
                _uiState.value = ExploreUiState.Loading
            }

            try {
                val smurfs = repository.getAllSmurfs()

                if (smurfs.isEmpty()) {
                    _uiState.value = ExploreUiState.Error("No characters found in the village.")
                } else {
                    _uiState.value = ExploreUiState.Loaded(smurfs, isRefreshing = false)
                }

            } catch (e: Exception) {
                _uiState.value = ExploreUiState.Error(e.message ?: "Failed to load smurfs from Firebase")
            }
        }
    }
}
