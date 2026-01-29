package com.bih.applicationsmurfforyou.presentation.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * A helper class to manage loading and showing interstitial ads.
 * This encapsulates the ad logic to keep it separate from UI and ViewModel.
 * This class now implements a more robust load-then-show pattern.
 */
class InterstitialAdManager(private val context: Context) {

    // Use the official test ad unit ID for development.
    // Replace with your real ad unit ID for production.
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    /**
     * Loads an ad and then shows it.
     * This is the main entry point for showing an ad.
     * If the ad fails to load, it will simply log the error and not block the user.
     */
    fun loadAndShowAd(activity: Activity) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                // The ad is loaded and ready to be shown.
                interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    // Called when the ad is dismissed by the user.
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("AdManager", "Ad was dismissed.")
                    }
                }
                interstitialAd.show(activity)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // The ad failed to load. We log the error but don't block the user.
                // The AI processing in the background will continue as normal.
                Log.e("AdManager", "Ad failed to load: ${loadAdError.message}")
            }
        })
    }
}
