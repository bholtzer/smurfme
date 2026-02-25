package com.bih.applicationsmurfforyou.app

import android.app.Application
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import com.bih.applicationsmurfforyou.presentation.language.LocaleManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SmurfApp : Application() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize the locale when the app starts
        LocaleManager.initLocale(settingsRepository)
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this)
    }
}
