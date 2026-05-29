package com.dfjk.retouch.member

import android.content.Context
import android.content.SharedPreferences

class MembershipStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("membership", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_MEMBER = "is_member"
        private const val KEY_HD_UNLOCK_TIME = "hd_unlock_time"
        private const val KEY_VIDEO_UNLOCK_TIME = "video_unlock_time"
        private const val KEY_INTERSTITIAL_LAST_TIME = "interstitial_last_time"
        private const val INTERSTITIAL_INTERVAL_MS = 90 * 1000L
        private const val REWARD_UNLOCK_DURATION_MS = 60 * 60 * 1000L
    }

    fun isMember(): Boolean = prefs.getBoolean(KEY_IS_MEMBER, false)

    fun setMember(isMember: Boolean) {
        prefs.edit().putBoolean(KEY_IS_MEMBER, isMember).apply()
    }

    fun canExportHD(): Boolean {
        if (isMember()) return true
        val unlockedTime = prefs.getLong(KEY_HD_UNLOCK_TIME, 0)
        return System.currentTimeMillis() < unlockedTime
    }

    fun unlockHDExport() {
        val unlockedTime = System.currentTimeMillis() + REWARD_UNLOCK_DURATION_MS
        prefs.edit().putLong(KEY_HD_UNLOCK_TIME, unlockedTime).apply()
    }

    fun canExportVideo(): Boolean {
        if (isMember()) return true
        val unlockedTime = prefs.getLong(KEY_VIDEO_UNLOCK_TIME, 0)
        return System.currentTimeMillis() < unlockedTime
    }

    fun unlockVideoExport() {
        val unlockedTime = System.currentTimeMillis() + REWARD_UNLOCK_DURATION_MS
        prefs.edit().putLong(KEY_VIDEO_UNLOCK_TIME, unlockedTime).apply()
    }

    fun shouldShowInterstitial(): Boolean {
        if (isMember()) return false
        val lastTime = prefs.getLong(KEY_INTERSTITIAL_LAST_TIME, 0)
        val now = System.currentTimeMillis()
        return (now - lastTime) >= INTERSTITIAL_INTERVAL_MS
    }

    fun recordInterstitialShow() {
        prefs.edit()
            .putLong(KEY_INTERSTITIAL_LAST_TIME, System.currentTimeMillis())
            .apply()
    }
}