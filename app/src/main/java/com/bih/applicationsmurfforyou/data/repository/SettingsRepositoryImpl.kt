package com.bih.applicationsmurfforyou.data.repository

import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getLanguageCode(): Flow<String> {
        return localDataSource.languageCode
    }

    override suspend fun setLanguageCode(languageCode: String) {
        localDataSource.setLanguageCode(languageCode)
    }
}
