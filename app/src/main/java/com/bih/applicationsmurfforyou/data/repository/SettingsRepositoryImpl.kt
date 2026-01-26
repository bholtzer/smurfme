package com.bih.applicationsmurfforyou.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    override fun getLanguageCode(): Flow<String> {
        return context.dataStore.data.map {
            it[PreferencesKeys.LANGUAGE_CODE] ?: ""
        }
    }

    override suspend fun setLanguageCode(languageCode: String) {
        context.dataStore.edit {
            it[PreferencesKeys.LANGUAGE_CODE] = languageCode
        }
    }
}
