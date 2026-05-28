package com.dfjk.retouch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dfjk.retouch.ads.AdManager
import com.dfjk.retouch.member.MembershipActivity
import com.dfjk.retouch.photo.PhotoEditorActivity
import com.dfjk.retouch.video.VideoWatermarkActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {
    private lateinit var btnPhoto: Button
    private lateinit var btnVideo: Button
    private lateinit var btnMembership: Button
    private lateinit var adView: AdView
    private val PICK_IMAGE_REQUEST = 1001
    private val PICK_VIDEO_REQUEST = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPhoto = findViewById(R.id.btnPhoto)
        btnVideo = findViewById(R.id.btnVideo)
        btnMembership = findViewById(R.id.btnMembership)
        adView = findViewById(R.id.bannerAdView)
        AdManager.initUMPAndAds(this) { success ->
            if (success) {
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        }
        btnPhoto.setOnClickListener { pickImage() }
        btnVideo.setOnClickListener { pickVideo() }
        btnMembership.setOnClickListener {
            startActivity(Intent(this, MembershipActivity::class.java))
        }
        AdManager.loadInterstitialAd(this)
        AdManager.loadRewardedAd(this)
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "video/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                data?.data?.let { uri ->
                    val intent = Intent(this, PhotoEditorActivity::class.java).apply {
                        putExtra("image_uri", uri)
                    }
                    startActivity(intent)
                }
            }
            PICK_VIDEO_REQUEST -> {
                data?.data?.let { uri ->
                    val intent = Intent(this, VideoWatermarkActivity::class.java).apply {
                        putExtra("video_uri", uri)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}