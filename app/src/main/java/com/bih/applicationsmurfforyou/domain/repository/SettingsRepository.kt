package com.bih.applicationsmurfforyou.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user preferences, such as language.
 */
interface SettingsRepository {
    /**
     * Gets the user's currently selected language code (e.g., "en", "es").
     * Returns a flow that emits the language code whenever it changes.
     */
    fun getLanguageCode(): Flow<String>

    /**
     * Sets and persists the user's selected language code.
     */
    suspend fun setLanguageCode(languageCode: String)
}
