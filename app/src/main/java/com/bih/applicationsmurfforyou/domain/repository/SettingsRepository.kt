package com.bih.applicationsmurfforyou.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user preferences.
 */
interface SettingsRepository {
    /**
     * Gets the user's preferred language code (e.g., "en", "es").
     */
    fun getLanguage(): Flow<String>

    /**
     * Sets the user's preferred language code.
     */
    suspend fun setLanguage(languageCode: String)
}
