package com.bih.applicationsmurfforyou.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import com.bih.applicationsmurfforyou.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the saved language on startup and whenever it changes
        settingsRepository.getLanguageCode()
            .onEach { languageCode ->
                if (languageCode.isNotEmpty()) {
                    val localeList = LocaleListCompat.forLanguageTags(languageCode)
                    AppCompatDelegate.setApplicationLocales(localeList)
                }
            }
            .launchIn(lifecycleScope)

        setContent {
            SmurfApp()
        }
    }
}

@Composable
fun SmurfApp() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
}
