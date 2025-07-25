package com.example.musicapp.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicapp.data.model.Song
import com.example.musicapp.notification.MusicNotificationManager

class MusicService : Service() {
    companion object {
        const val TAG = "MusicServiceDebug"
        const val ACTION_PLAY = "com.example.musicapp.PLAY"
        const val ACTION_PAUSE = "com.example.musicapp.PAUSE"
        const val ACTION_NEXT = "com.example.musicapp.NEXT"
        const val ACTION_PREV = "com.example.musicapp.PREV"
        const val ACTION_STOP = "com.example.musicapp.STOP"
        const val EXTRA_SONG = "extra_song"
        const val EXTRA_SONG_LIST = "extra_song_list"
        const val EXTRA_INDEX = "extra_index"
        const val BROADCAST_STATE = "com.example.musicapp.STATE"
        const val EXTRA_STATE = "extra_state"
        const val STATE_PLAYING = "playing"
        const val STATE_PAUSED = "paused"
        const val BROADCAST_PROGRESS = "com.example.musicapp.PROGRESS"
        const val EXTRA_PROGRESS = "extra_progress"
        const val EXTRA_DURATION = "extra_duration"
    }

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: Song? = null
    private var songList: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private lateinit var notificationManager: MusicNotificationManager
    private val handler = Handler()
    private val progressRunnable = object : Runnable {
        override fun run() {
            if (isPrepared) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        sendProgressBroadcast(it.currentPosition, it.duration)
                    }
                }
            }
            handler.postDelayed(this, 500)
        }
    }
    private var isPrepared = false

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = MusicNotificationManager(this)
        Log.d(TAG, "Service created")
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: action=${intent?.action}")
        when (intent?.action) {
            ACTION_PLAY -> {
                val song = intent.getSerializableExtra(EXTRA_SONG) as? Song
                val list = intent.getSerializableExtra(EXTRA_SONG_LIST) as? ArrayList<Song>
                val index = intent.getIntExtra(EXTRA_INDEX, -1)
                Log.d(TAG, "ACTION_PLAY: song=$song, listSize=${list?.size}, index=$index")
                if (song != null && list != null && index != -1) {
                    playSong(song, list, index)
                } else {
                    resume()
                }
            }
            ACTION_PAUSE -> {
                Log.d(TAG, "ACTION_PAUSE")
                if (isPrepared) pause()
            }
            ACTION_NEXT -> {
                Log.d(TAG, "ACTION_NEXT")
                playNext()
            }
            ACTION_PREV -> {
                Log.d(TAG, "ACTION_PREV")
                playPrev()
            }
            ACTION_STOP -> {
                Log.d(TAG, "ACTION_STOP")
                stopSelf()
            }
            "com.example.musicapp.SEEK_TO" -> {
                val seekTo = intent.getIntExtra("seek_to", 0)
                val duration = mediaPlayer?.duration ?: 0
                if (isPrepared && duration > 0 && seekTo < duration) {
                    mediaPlayer?.seekTo(seekTo)
                }
            }
        }
        return START_STICKY
    }

    fun playSong(song: Song, list: ArrayList<Song>, index: Int) {
        Log.d(TAG, "playSong: $song, index=$index, listSize=${list.size}")
        currentSong = song
        songList = list
        currentIndex = index
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        isPrepared = false
        try {
            if (song.url.startsWith("android.resource://")) {
                Log.d(TAG, "playSong: setDataSource local resource: ${song.url}")
                mediaPlayer?.setDataSource(this, android.net.Uri.parse(song.url))
                mediaPlayer?.prepare()
                isPrepared = true
                mediaPlayer?.start()
            } else {
                Log.d(TAG, "playSong: setDataSource url: ${song.url}")
                mediaPlayer?.setDataSource(song.url)
                mediaPlayer?.setOnPreparedListener {
                    isPrepared = true
                    it.start()
                }
                mediaPlayer?.prepareAsync()
            }
            // Gửi notification khi phát nhạc
            val notification = notificationManager.getNotification(song, isPlaying = true)
            Log.d(TAG, "startForeground called")
            startForeground(MusicNotificationManager.NOTIFICATION_ID, notification)
            mediaPlayer?.setOnCompletionListener { playNext() }
            sendStateBroadcast(STATE_PLAYING)
            handler.post(progressRunnable)
        } catch (e: Exception) {
            Log.e(TAG, "MediaPlayer error: ${e.message}", e)
        }
    }

    fun playNext() {
        Log.d(TAG, "playNext")
        if (songList.isNotEmpty() && currentIndex < songList.size - 1) {
            currentIndex++
            playSong(songList[currentIndex], songList as ArrayList<Song>, currentIndex)
        }
    }

    fun playPrev() {
        Log.d(TAG, "playPrev")
        if (songList.isNotEmpty() && currentIndex > 0) {
            currentIndex--
            playSong(songList[currentIndex], songList as ArrayList<Song>, currentIndex)
        }
    }

    fun pause() {
        Log.d(TAG, "pause")
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            // Gửi progress hiện tại về Activity
            mediaPlayer?.let {
                sendProgressBroadcast(it.currentPosition, it.duration)
            }
            // Cập nhật notification trạng thái pause
            currentSong?.let {
                val notification = notificationManager.getNotification(it, isPlaying = false)
                startForeground(MusicNotificationManager.NOTIFICATION_ID, notification)
                sendStateBroadcast(STATE_PAUSED)
            }
            handler.removeCallbacks(progressRunnable)
        } else {
            Log.d(TAG, "pause ignored: mediaPlayer is not playing")
        }
    }

    fun resume() {
        Log.d(TAG, "resume")
        mediaPlayer?.start()
        // Cập nhật notification trạng thái play
        currentSong?.let {
            val notification = notificationManager.getNotification(it, isPlaying = true)
            startForeground(MusicNotificationManager.NOTIFICATION_ID, notification)
            sendStateBroadcast(STATE_PLAYING)
        }
        handler.post(progressRunnable)
    }

    private fun sendStateBroadcast(state: String) {
        val intent = Intent(BROADCAST_STATE)
        intent.putExtra(EXTRA_STATE, state)
        intent.putExtra(EXTRA_SONG, currentSong)
        intent.putExtra(EXTRA_INDEX, currentIndex)
        intent.putExtra(EXTRA_SONG_LIST, ArrayList(songList))
        intent.putExtra("is_prepared", isPrepared) // Thêm trạng thái isPrepared
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendProgressBroadcast(progress: Int, duration: Int) {
        val intent = Intent(BROADCAST_PROGRESS)
        intent.putExtra(EXTRA_PROGRESS, progress)
        intent.putExtra(EXTRA_DURATION, duration)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(progressRunnable)
    }
} 