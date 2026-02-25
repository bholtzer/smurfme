package com.bih.applicationsmurfforyou.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
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
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        loadSupportedLanguages()
        loadCurrentLanguage()
    }

    private fun loadSupportedLanguages() {
        // In a real app, you would load these from a remote config or a local database.
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
            // First, save the selected language to our repository
            settingsRepository.setLanguageCode(languageCode)
            // Then, apply the change to the app. This will trigger the Activity to be recreated.
            LocaleManager.setLocale(languageCode)
        }
    }
}
