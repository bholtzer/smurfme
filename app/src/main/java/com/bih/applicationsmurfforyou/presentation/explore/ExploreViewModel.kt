package com.bih.applicationsmurfforyou.presentation.explore


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.repository.SmurfRepositoryImpl
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.bih.applicationsmurfforyou.domain.usecase.GetAllSmurfsUseCase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getAllSmurfs: GetAllSmurfsUseCase,
    private val repository: SmurfRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Idle)
    val uiState: StateFlow<ExploreUiState> = _uiState

    private val _smurfs = MutableStateFlow<List<Smurf>>(emptyList())
    val smurfs: StateFlow<List<Smurf>> = _smurfs

    init {
        loadSmurfs()
    }

    private fun loadSmurfs() = viewModelScope.launch {
        _uiState.value = ExploreUiState.Loading
        try {
        viewModelScope.launch {
            _smurfs.value = repository.getAllSmurfsCached()

            //_uiState.value =
                if (smurfs.value.isEmpty()) {
                    _uiState.value = ExploreUiState.Loading
                    _smurfs.value = repository.getAllSmurfs()
                    _uiState.value = ExploreUiState.Loaded(_smurfs.value)

                } else {
                    _uiState.value = ExploreUiState.Loaded(_smurfs.value)
                }
                        //as ExploreUiState

        }
        } catch (e: Exception) {
            _uiState.value = ExploreUiState.Error(e.message ?: "Failed to load smurfs")
             Log.e("ExploreViewModel", "loadSmurfs error: ${e.message}")
        }
    }

    fun refresh() {
        loadSmurfs()
    }
}