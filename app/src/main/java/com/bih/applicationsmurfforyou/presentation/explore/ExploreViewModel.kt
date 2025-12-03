package com.bih.applicationsmurfforyou.presentation.explore


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.usecase.GetAllSmurfsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getAllSmurfs: GetAllSmurfsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Idle)
    val uiState: StateFlow<ExploreUiState> = _uiState

    init {
        loadSmurfs()
    }

    private fun loadSmurfs() = viewModelScope.launch {
        _uiState.value = ExploreUiState.Loading

         try {
            val smurfs = getAllSmurfs()
            _uiState.value = ExploreUiState.Loaded(smurfs)
        } catch (e: Exception) {
            _uiState.value = ExploreUiState.Error(e.message ?: "Failed to load smurfs")
        }
    }
}