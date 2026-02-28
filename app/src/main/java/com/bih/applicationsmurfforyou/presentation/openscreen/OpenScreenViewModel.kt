package com.bih.applicationsmurfforyou.presentation.openscreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

// The state model for THIS screen only.
sealed class PreloadState {
    object Idle : PreloadState()
    data class Loading(val progress: Int) : PreloadState() // Loading state now holds progress data
    object Success : PreloadState()
    data class Error(val message: String) : PreloadState()
}

@HiltViewModel
class OpenScreenViewModel @Inject constructor(
    private val repository: SmurfRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _preloadState = MutableStateFlow<PreloadState>(PreloadState.Idle)
    val preloadState: StateFlow<PreloadState> = _preloadState

    companion object {
        private const val TAG = "OpenScreenViewModel"
    }

    init {
        beginPreloading()
    }

    private fun beginPreloading() {
        viewModelScope.launch {
            try {
                _preloadState.value = PreloadState.Loading(0)

                val smurfs = fetchSmurfListWithRetries(maxRetries = 3)

                if (smurfs.isEmpty()) {
                    _preloadState.value = PreloadState.Error("Failed to fetch character list. Check your internet connection.")
                    return@launch
                }

                preloadImagesAndProceed(smurfs)

                _preloadState.value = PreloadState.Success

            } catch (e: Exception) {
                Log.e(TAG, "Preloading failed: ${e.message}")
                _preloadState.value = PreloadState.Error("Failed to connect. Please check internet and Firebase config.")
            }
        }
    }

    private suspend fun fetchSmurfListWithRetries(maxRetries: Int): List<Smurf> {
        for (attempt in 1..maxRetries) {
            try {
                val smurfs = repository.getAllSmurfs()
                if (smurfs.isNotEmpty()) return smurfs
            } catch (e: Exception) {
                Log.w(TAG, "Fetch attempt $attempt failed: ${e.message}")
                delay(1500L) // Wait before retrying
            }
        }
        return emptyList()
    }

    private suspend fun preloadImagesAndProceed(smurfs: List<Smurf>) {
        val imageLoader = context.imageLoader
        val totalImages = smurfs.size
        val processedCount = AtomicInteger(0)

        coroutineScope {
            for (character in smurfs) {
                launch {
                    try {
                        // Use the correct `imageUrl` field from the Smurf data class
                        character.imageUrl?.let {
                            withTimeoutOrNull(5000L) {
                                val request = ImageRequest.Builder(context).data(it).build()
                                imageLoader.execute(request)
                            }
                        }
                    } finally {
                        val currentProcessed = processedCount.incrementAndGet()
                        val progress = (currentProcessed * 100) / totalImages
                        _preloadState.value = PreloadState.Loading(progress)
                    }
                }
            }
        }
    }
}
