package com.bih.applicationsmurfforyou.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getLanguageCode(): Flow<String>
    suspend fun setLanguageCode(languageCode: String)
}
