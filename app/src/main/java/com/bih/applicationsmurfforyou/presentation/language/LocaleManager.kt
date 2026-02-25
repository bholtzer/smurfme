package com.bih.applicationsmurfforyou.presentation.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object LocaleManager {

    fun initLocale(settingsRepository: SettingsRepository) {
        val languageCode = runBlocking {
            settingsRepository.getLanguageCode().first()
        }
        setLocale(languageCode)
    }

    fun setLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
