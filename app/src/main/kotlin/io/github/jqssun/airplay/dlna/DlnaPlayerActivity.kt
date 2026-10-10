package io.github.jqssun.airplay.dlna

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/** Full-screen ExoPlayer that renders media URLs pushed to the DLNA renderer. */
class DlnaPlayerActivity : Activity(), DlnaController.PlayerControl {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var imageView: android.widget.ImageView? = null
    private var shownUri: String? = null
    private var photo = false
    private var generation = 0
    private val imageWorker = java.util.concurrent.ThreadPoolExecutor(1, 1, 0L, java.util.concurrent.TimeUnit.MILLISECONDS, java.util.concurrent.LinkedBlockingQueue<Runnable>())
    private val handler = Handler(Looper.getMainLooper())

    private val progressTick = object : Runnable {
        override fun run() {
            player?.let {
                DlnaController.positionMs = it.currentPosition.coerceAtLeast(0)
                DlnaController.durationMs = if (it.duration > 0) it.duration else 0
            }
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val view = PlayerView(this).apply {
            useController = true
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        }
        playerView = view
        val frame = android.widget.FrameLayout(this)
        frame.setBackgroundColor(android.graphics.Color.BLACK)
        frame.addView(view, android.widget.FrameLayout.LayoutParams(-1, -1))
        imageView = android.widget.ImageView(this).apply { scaleType = android.widget.ImageView.ScaleType.FIT_CENTER; visibility = android.view.View.GONE }
        frame.addView(imageView, android.widget.FrameLayout.LayoutParams(-1, -1))
        setContentView(frame)
        window.decorView.post { goFullscreen() }

        val exo = ExoPlayer.Builder(this).build()
        player = exo
        view.player = exo
        exo.setVolume(DlnaController.volume / 100f)
        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) = syncState()
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) = syncState()
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                DlnaController.transportState = DlnaController.STATE_STOPPED
            }
        })

        DlnaController.player = this
        playUri(DlnaController.uri ?: intent?.getStringExtra(EXTRA_URI))
        handler.post(progressTick)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        playUri(DlnaController.uri ?: intent?.getStringExtra(EXTRA_URI))
    }

    private fun playUri(uri: String?) {
        val p = player ?: return
        if (uri.isNullOrBlank() || !MediaUrls.allowed(uri)) return
        shownUri = uri
        val version = ++generation
        val path = android.net.Uri.parse(uri).path.orEmpty().lowercase()
        photo = path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") ||
            DlnaController.metadata.contains("object.item.imageItem") ||
            DlnaController.metadata.contains("image/jpeg") || DlnaController.metadata.contains("image/png")
        imageView?.setImageDrawable(null)
        imageView?.visibility = if (photo) android.view.View.VISIBLE else android.view.View.GONE
        playerView?.visibility = if (photo) android.view.View.GONE else android.view.View.VISIBLE
        if (photo) {
            p.stop()
            DlnaController.positionMs = 0
            DlnaController.durationMs = 0
            DlnaController.transportState = DlnaController.STATE_TRANSITIONING
            // One bounded download and one decoded 1080p image; never decode a full camera original.
            imageWorker.queue.clear()
            imageWorker.execute {
                try {
                    val connection = java.net.URL(uri).openConnection() as java.net.HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.instanceFollowRedirects = false
                    val bytes = try {
                        require(connection.responseCode == 200)
                        require(connection.contentLengthLong <= 12 * 1024 * 1024)
                        connection.inputStream.use { input ->
                            val out = java.io.ByteArrayOutputStream()
                            val chunk = ByteArray(16384)
                            while (true) {
                                val n = input.read(chunk)
                                if (n < 0) break
                                require(out.size() + n <= 12 * 1024 * 1024)
                                out.write(chunk, 0, n)
                            }
                            out.toByteArray()
                        }
                    } finally { connection.disconnect() }
                    val options = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    require(options.outWidth > 0 && options.outHeight > 0)
                    var sample = 1
                    while (options.outWidth / sample > 1920 || options.outHeight / sample > 1080) sample *= 2
                    options.inJustDecodeBounds = false
                    options.inSampleSize = sample
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        ?: error("Unsupported photo")
                    handler.post {
                        if (generation == version && !isDestroyed) {
                            imageView?.setImageBitmap(bitmap)
                            DlnaController.transportState = DlnaController.STATE_PLAYING
                        } else bitmap.recycle()
                    }
                } catch (_: Exception) {
                    handler.post {
                        if (generation == version && !isDestroyed) {
                            DlnaController.transportState = DlnaController.STATE_STOPPED
                            android.widget.Toast.makeText(this, "Could not display this photo. Try a JPEG or PNG under 12 MB.", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else {
            p.setMediaItem(MediaItem.fromUri(uri))
            p.prepare()
            p.playWhenReady = true
        }
    }

    private fun syncState() {
        if (photo) return
        val p = player ?: return
        DlnaController.transportState = when (p.playbackState) {
            Player.STATE_BUFFERING -> DlnaController.STATE_TRANSITIONING
            Player.STATE_READY -> if (p.playWhenReady) DlnaController.STATE_PLAYING else DlnaController.STATE_PAUSED
            Player.STATE_ENDED -> DlnaController.STATE_STOPPED
            else -> DlnaController.STATE_STOPPED
        }
    }

    // ---- DlnaController.PlayerControl (called from renderer threads) ----
    override fun play() = handler.post {
        if (shownUri != DlnaController.uri) playUri(DlnaController.uri)
        else if (photo) DlnaController.transportState = DlnaController.STATE_PLAYING
        else player?.play()
    }.let {}
    override fun pause() = handler.post { player?.pause() }.let {}
    override fun stop() = handler.post { finish() }.let {}
    override fun seekTo(ms: Long) = handler.post { player?.seekTo(ms) }.let {}
    override fun setVolume(percent: Int) = handler.post { player?.setVolume(percent / 100f) }.let {}

    private fun goFullscreen() {
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(android.view.WindowInsets.Type.systemBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        generation++
        imageWorker.shutdownNow()
        imageView?.setImageDrawable(null)
        handler.removeCallbacksAndMessages(null)
        if (DlnaController.player === this) DlnaController.player = null
        DlnaController.transportState = DlnaController.STATE_STOPPED
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_URI = "dlna_uri"
    }
}
