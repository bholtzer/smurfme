package com.bih.applicationsmurfforyou.app

import android.app.Application
import com.bih.applicationsmurfforyou.BuildConfig
import com.bih.applicationsmurfforyou.domain.repository.SettingsRepository
import com.bih.applicationsmurfforyou.presentation.language.LocaleManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SmurfApp : Application() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        
        Firebase.initialize(context = this)
        val firebaseAppCheck = Firebase.appCheck
        firebaseAppCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )

        // Initialize the locale when the app starts
        LocaleManager.initLocale(settingsRepository)
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this)
    }
}
