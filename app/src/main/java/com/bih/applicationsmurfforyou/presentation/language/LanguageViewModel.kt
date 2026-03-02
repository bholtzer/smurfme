package com.bih.applicationsmurfforyou.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageUiState(
    val supportedLanguages: List<Language> = emptyList(),
    val selectedLanguageCode: String = "en"
)

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val analytics: FirebaseAnalytics
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        loadSupportedLanguages()
        loadCurrentLanguage()
        logScreenView()
    }

    private fun logScreenView() {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Language Selection")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "LanguageViewModel")
        }
    }

    private fun loadSupportedLanguages() {
        val languages = listOf(
            Language("English", "en"),
            Language("Español", "es"),
            Language("Français", "fr"),
            Language("Deutsch", "de"),
            Language("עברית", "iw"),
            Language("العربية", "ar")
        )
        _uiState.value = _uiState.value.copy(supportedLanguages = languages)
    }

    private fun loadCurrentLanguage() {
        settingsRepository.getLanguageCode()
            .onEach { languageCode ->
                _uiState.value = _uiState.value.copy(selectedLanguageCode = languageCode)
            }
            .launchIn(viewModelScope)
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            analytics.logEvent("language_changed") {
                param("new_language", languageCode)
            }
            settingsRepository.setLanguageCode(languageCode)
            LocaleManager.setLocale(languageCode)
        }
    }
}
