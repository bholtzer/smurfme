package com.bih.applicationsmurfforyou.presentation.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Language(val code: String, val name: String)

data class LanguageUiState(
    val supportedLanguages: List<Language>,
    val selectedLanguageCode: String
)

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val supportedLanguages = listOf(
        Language("", "System Default"),
        Language("en", "English"),
        Language("es", "Español"),
        Language("fr", "Français"),
        Language("he", "עברית"),
        Language("ar", "العربية")
    )

    val uiState: StateFlow<LanguageUiState> = settingsRepository.getLanguageCode()
        .map { currentLanguageCode ->
            LanguageUiState(
                supportedLanguages = supportedLanguages,
                selectedLanguageCode = currentLanguageCode
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LanguageUiState(supportedLanguages, "")
        )

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            settingsRepository.setLanguageCode(languageCode)
        }
    }
}
