package com.mobnews.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.mobnews.app.R

class ExoPlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var isFullScreen = false

    private val newsUrls = arrayOf(
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)
        playerView = findViewById(R.id.player_view)
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).apply {

        }.build()
            playerView.player =player

        val shuffledUrls = newsUrls.toList().shuffled()
        for (url in shuffledUrls) {
            val mediaItem = MediaItem.Builder().setUri(url).build()
            player!!.addMediaItem(mediaItem)
        }
            player!!.prepare()
        player!!.playWhenReady = false

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            hideSystemUI()
        }
    }
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        actionBar?.hide()
    }
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        actionBar?.show()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            showSystemUI()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let {
            it.release()
            player = null
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (isFullScreen) {
                hideSystemUI()
            }
        } else {
            if (isFullScreen) {
                showSystemUI()
            }
        }
    }

    fun toggleFullScreen() {
        if (isFullScreen) {
            showSystemUI()
        } else {
            hideSystemUI()
        }
        isFullScreen = !isFullScreen
    }

}