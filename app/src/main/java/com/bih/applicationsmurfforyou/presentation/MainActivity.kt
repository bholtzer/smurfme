package com.bih.applicationsmurfforyou.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import com.bih.applicationsmurfforyou.presentation.navigation.AppNavGraph
import com.bih.applicationsmurfforyou.presentation.ui.components.ErrorMessage
import com.bih.applicationsmurfforyou.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var networkManager: NetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the saved language on startup
        lifecycleScope.launch {
            val languageCode = settingsRepository.getLanguageCode().first()
            if (languageCode.isNotEmpty()) {
                val localeList = LocaleListCompat.forLanguageTags(languageCode)
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        }

        setContent {
            val isNetworkAvailable by networkManager.isNetworkAvailable.collectAsState()
            if (isNetworkAvailable) {
                SmurfApp()
            } else {
                ErrorMessage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkManager.unregister()
    }
}

@Composable
fun SmurfApp() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
}
