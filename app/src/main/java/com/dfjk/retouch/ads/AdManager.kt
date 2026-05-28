package com.dfjk.retouch.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

object AdManager {
    private const val TAG = "AdManager"
    const val BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917"
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    fun initUMPAndAds(context: Context, callback: (Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()
        ConsentInformation.getInstance(context).requestConsentInfoUpdate(
            context as? Activity ?: return,
            params,
            {
                Log.d(TAG, "UMP consent info updated")
                UserMessagingPlatform.loadConsentForm(
                    context,
                    { form ->
                        if (ConsentInformation.getInstance(context).consentStatus ==
                            ConsentInformation.ConsentStatus.REQUIRED
                        ) {
                            form.show(context) {
                                Log.d(TAG, "Consent form shown")
                                initMobileAds(context)
                                callback(true)
                            }
                        } else {
                            initMobileAds(context)
                            callback(true)
                        }
                    },
                    { error ->
                        Log.e(TAG, "Consent form load failed: ${error.message}")
                        initMobileAds(context)
                        callback(true)
                    }
                )
            },
            { error ->
                Log.e(TAG, "Consent info request failed: ${error.message}")
                initMobileAds(context)
                callback(true)
            }
        )
    }

    private fun initMobileAds(context: Context) {
        MobileAds.initialize(context)
        Log.d(TAG, "Mobile Ads initialized")
    }

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    super.onAdLoaded(ad)
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial ad loaded")
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    interstitialAd = null
                    Log.e(TAG, "Interstitial ad failed: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onDismiss: () -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd!!.fullScreenContentCallback = object :
                com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial dismissed")
                    interstitialAd = null
                    onDismiss()
                    loadInterstitialAd(activity)
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Interstitial show failed: ${error.message}")
                    interstitialAd = null
                    onDismiss()
                }
            }
            interstitialAd!!.show(activity)
        } else {
            onDismiss()
            loadInterstitialAd(activity)
        }
    }

    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    super.onAdLoaded(ad)
                    rewardedAd = ad
                    Log.d(TAG, "Rewarded ad loaded")
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    rewardedAd = null
                    Log.e(TAG, "Rewarded ad failed: ${loadAdError.message}")
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity, onReward: () -> Unit) {
        if (rewardedAd != null) {
            rewardedAd!!.fullScreenContentCallback = object :
                com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Rewarded dismissed")
                    rewardedAd = null
                    loadRewardedAd(activity)
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Rewarded show failed: ${error.message}")
                    rewardedAd = null
                }
            }
            rewardedAd!!.show(activity) {
                Log.d(TAG, "User earned reward")
                onReward()
            }
        } else {
            loadRewardedAd(activity)
        }
    }
}