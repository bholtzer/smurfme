package com.bih.applicationsmurfforyou.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import com.bih.applicationsmurfforyou.presentation.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

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
            SmurfApp()
        }
    }
}

@Composable
fun SmurfApp() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
}
