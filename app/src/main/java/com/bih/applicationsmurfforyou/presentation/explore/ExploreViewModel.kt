package com.bih.applicationsmurfforyou.presentation.explore

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.util.ConnectivityObserver
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

sealed class ExploreEvent {
    data class NavigateToDetail(val smurfName: String) : ExploreEvent()
    object ShowGargamelTrap : ExploreEvent()
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: SmurfRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val analytics: FirebaseAnalytics
) : ViewModel() {

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

    private val _eventFlow = MutableSharedFlow<ExploreEvent>()
    val eventFlow: SharedFlow<ExploreEvent> = _eventFlow.asSharedFlow()

    private var clickCount = 0
    private var trapThreshold = Random.nextInt(1, 11)

    init {
        if (connectivityObserver.isNetworkAvailable()) {
            loadSmurfs(forceRefresh = false)
        }
        observeNetworkStatus()
        logScreenView()
    }

    private fun logScreenView() {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Explore Screen")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "ExploreViewModel")
        }
    }

    private fun observeNetworkStatus() {
        connectivityObserver.observe().onEach { status ->
            val oldStatus = _networkStatus.value
            _networkStatus.value = status
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
                analytics.logEvent("refresh_village") {}
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
                analytics.logEvent("explore_error") {
                    param("error_msg", e.message ?: "unknown")
                }
            } finally {
                if (forceRefresh) {
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun toggleLayout() {
        _isGridLayout.update { !it }
        analytics.logEvent("toggle_layout") {
            param("layout_mode", if (_isGridLayout.value) "grid" else "list")
        }
    }

    fun onSmurfClick(smurfName: String) {
        clickCount++
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_NAME, smurfName)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "smurf_character")
        }

        if (clickCount >= trapThreshold) {
            clickCount = 0
            trapThreshold = Random.nextInt(1, 11)
            analytics.logEvent("trap_triggered") {}
            viewModelScope.launch {
                _eventFlow.emit(ExploreEvent.ShowGargamelTrap)
            }
        } else {
            viewModelScope.launch {
                _eventFlow.emit(ExploreEvent.NavigateToDetail(smurfName))
            }
        }
    }
    
    fun onNavigateToSmurfify() {
        analytics.logEvent("navigate_to_smurfify") {}
    }

    fun onNavigateToGallery() {
        analytics.logEvent("navigate_to_gallery") {}
    }
}
