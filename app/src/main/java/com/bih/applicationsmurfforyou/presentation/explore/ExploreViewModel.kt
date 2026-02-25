package com.bih.applicationsmurfforyou.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.util.ConnectivityObserver
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: SmurfRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    // Correctly set the initial state based on a synchronous network check.
    private val _smurfUiState = MutableStateFlow(
        if (connectivityObserver.isNetworkAvailable()) SmurfUiState.Loading
        else SmurfUiState.Error("No internet connection.")
    )
    val smurfUiState: StateFlow<SmurfUiState> = _smurfUiState.asStateFlow()

    private val _isGridLayout = MutableStateFlow(true)
    val isGridLayout: StateFlow<Boolean> = _isGridLayout.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _networkStatus = MutableStateFlow(
        if (connectivityObserver.isNetworkAvailable()) ConnectivityObserver.Status.Available
        else ConnectivityObserver.Status.Unavailable
    )
    val networkStatus: StateFlow<ConnectivityObserver.Status> = _networkStatus.asStateFlow()

    init {
        // Only attempt to load if we are starting online.
        if (connectivityObserver.isNetworkAvailable()) {
            loadSmurfs(forceRefresh = false)
        }
        // Observe for future network changes.
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        connectivityObserver.observe().onEach { status ->
            val oldStatus = _networkStatus.value
            _networkStatus.value = status
            // When network becomes available, and we were previously in an error state, load the data.
            if (status == ConnectivityObserver.Status.Available && oldStatus != ConnectivityObserver.Status.Available) {
                loadSmurfs(forceRefresh = false)
            }
        }.launchIn(viewModelScope)
    }

    fun loadSmurfs(forceRefresh: Boolean) {
        if (!connectivityObserver.isNetworkAvailable()) {
            _smurfUiState.value = SmurfUiState.Error("No internet connection.")
            if (forceRefresh) _isRefreshing.value = false
            return
        }

        viewModelScope.launch {
            if (forceRefresh) {
                _isRefreshing.value = true
            } else if (_smurfUiState.value !is SmurfUiState.Success) {
                _smurfUiState.value = SmurfUiState.Loading
            }

            try {
                val smurfs = repository.getAllSmurfs(forceRefresh)
                if (smurfs.isEmpty()) {
                    _smurfUiState.value = SmurfUiState.Error("No Smurfs found in the village.")
                } else {
                    _smurfUiState.value = SmurfUiState.Success(smurfs)
                }
            } catch (e: Exception) {
                _smurfUiState.value = SmurfUiState.Error(e.message ?: "An unknown error occurred.")
            } finally {
                if (forceRefresh) {
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun toggleLayout() {
        _isGridLayout.update { !it }
    }
}
