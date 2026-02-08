package com.bih.applicationsmurfforyou.presentation.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * A helper class to manage loading and showing interstitial ads.
 * This encapsulates the ad logic to keep it separate from UI and ViewModel.
 */
class InterstitialAdManager(private val context: Context) {

    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    /**
     * Loads an ad and then shows it.
     * The onAdDismissedOrFailed callback is guaranteed to be called once, either after the ad is
     * dismissed, or if the ad fails to load or show.
     */
    fun loadAndShowAd(activity: Activity, onAdDismissedOrFailed: () -> Unit) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // The ad was dismissed. Proceed with the app flow.
                        Log.d("AdManager", "Ad was dismissed.")
                        onAdDismissedOrFailed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // The ad failed to show. Proceed with the app flow.
                        Log.e("AdManager", "Ad failed to show: ${adError.message}")
                        onAdDismissedOrFailed()
                    }
                }
                interstitialAd.show(activity)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // The ad failed to load. We don't block the user; proceed with the app flow.
                Log.e("AdManager", "Ad failed to load: ${loadAdError.message}")
                onAdDismissedOrFailed()
            }
        })
    }
}
