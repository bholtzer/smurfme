package com.bih.applicationsmurfforyou.presentation.openscreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PreloadState {
    object Idle : PreloadState()
    data class Loading(val progress: Int) : PreloadState()
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
        // --- TEMPORARY DEBUGGING CODE TO SHOW PROGRESS BAR ---
        // This code simulates a successful load to prove the UI is working.
        // It should be removed after you fix your Firebase configuration.
        viewModelScope.launch {
            _preloadState.value = PreloadState.Loading(0)
            for (i in 1..100) {
                delay(40) // Simulate network and image loading time
                _preloadState.value = PreloadState.Loading(i)
            }
            _preloadState.value = PreloadState.Success
        }
    }

    /*
    // --- ORIGINAL CODE (DISABLED FOR DEBUGGING) ---
    private fun beginPreloading() {
        viewModelScope.launch {
            try {
                _preloadState.value = PreloadState.Loading(0)

                val smurfs = fetchSmurfListWithRetries(maxRetries = 3)

                if (smurfs.isEmpty()) {
                    throw Exception("Failed to fetch character list. Check Firebase config and security rules.")
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
    */
}
