package com.bih.applicationsmurfforyou.presentation.ads

import android.app.Activity
import android.util.Log
import com.bih.applicationsmurfforyou.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val activity: Activity) {

    private var mInterstitialAd: InterstitialAd? = null

    init {
        loadAd()
    }

    private fun loadAd() {
        val adUnitId = BuildConfig.INTERSTITIAL_AD_UNIT_ID
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                Log.d("AdManager", "Ad was loaded.")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("AdManager", "Ad failed to load: $loadAdError")
                mInterstitialAd = null
            }
        })
    }

    fun showAd(onAdDismissed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdManager", "Ad was dismissed.")
                    onAdDismissed()
                    // Pre-load the next ad for the next time.
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AdManager", "Ad failed to show: ${adError.message}")
                    onAdDismissed() // Still call the callback to not block the user
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            Log.e("AdManager", "Ad was not ready to be shown.")
            onAdDismissed() // Ad wasn't shown, so just proceed with the app flow.
        }
    }
}
