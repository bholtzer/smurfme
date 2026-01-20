package com.bih.applicationsmurfforyou.presentation.smurf_detail

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed class SmurfDetailState {
    object Loading : SmurfDetailState()
    data class Loaded(
        val smurf: Smurf,
        val isSpeaking: Boolean = false
    ) : SmurfDetailState()
    data class Error(val message: String) : SmurfDetailState()
}

@HiltViewModel
class SmurfDetailViewModel @Inject constructor(
    private val repository: SmurfRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context
) : ViewModel(), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow<SmurfDetailState>(SmurfDetailState.Loading)
    val uiState: StateFlow<SmurfDetailState> = _uiState

    private var tts: TextToSpeech? = null

    init {
        viewModelScope.launch {
            val smurfName = savedStateHandle.get<String>("smurfName")
            if (smurfName == null) {
                _uiState.value = SmurfDetailState.Error("Smurf not found.")
                return@launch
            }
            val smurf = repository.getSmurfByName(smurfName)
            if (smurf == null) {
                _uiState.value = SmurfDetailState.Error("Smurf not found in cache.")
                return@launch
            }
            _uiState.value = SmurfDetailState.Loaded(smurf)
            tts = TextToSpeech(context, this@SmurfDetailViewModel)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    setSpeakingState(true)
                }
                override fun onDone(utteranceId: String?) {
                    setSpeakingState(false)
                }
                override fun onError(utteranceId: String?) {
                    setSpeakingState(false)
                }
            })
            speakDescription()
        } else {
            (_uiState.value as? SmurfDetailState.Loaded)?.let {
                _uiState.value = SmurfDetailState.Error("TTS initialization failed.")
            }
        }
    }

    private fun speakDescription() {
        (_uiState.value as? SmurfDetailState.Loaded)?.let {
            it.smurf.description?.let {
                tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "smurf_description")
            }
        }
    }

    private fun setSpeakingState(isSpeaking: Boolean) {
        (_uiState.value as? SmurfDetailState.Loaded)?.let {
            _uiState.value = it.copy(isSpeaking = isSpeaking)
        }
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }
}
