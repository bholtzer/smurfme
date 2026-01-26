package com.bih.applicationsmurfforyou.presentation.ads

import android.app.Activity
import android.content.Context
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

    private var mInterstitialAd: InterstitialAd? = null

    // Use the official test ad unit ID for development.
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    init {
        loadAd()
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                mInterstitialAd = null
            }
        })
    }

    fun showAd(activity: Activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Pre-load the next ad for the next time the user wants to smurfify an image.
                    mInterstitialAd = null
                    loadAd()
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            // If the ad wasn't loaded for any reason, just pre-load the next one.
            loadAd()
        }
    }
}
