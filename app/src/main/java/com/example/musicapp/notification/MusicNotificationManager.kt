                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    package com.example.musicapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.musicapp.R
import com.example.musicapp.data.model.Song
import com.example.musicapp.service.MusicService

class MusicNotificationManager(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "music_channel"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun getNotification(song: Song, isPlaying: Boolean, albumArt: Bitmap? = null): Notification {
        // PendingIntent cho các action
        val playIntent = Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_PLAY }
        val pauseIntent = Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_PAUSE }
        val nextIntent = Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_NEXT }
        val prevIntent = Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_PREV }
        val stopIntent = Intent(context, MusicService::class.java).apply { action = MusicService.ACTION_STOP }

        val playPendingIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pausePendingIntent = PendingIntent.getService(context, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val nextPendingIntent = PendingIntent.getService(context, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val prevPendingIntent = PendingIntent.getService(context, 3, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stopPendingIntent = PendingIntent.getService(context, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(albumArt)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2, 3))
            .addAction(android.R.drawable.ic_media_previous, "Prev", prevPendingIntent)
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "Pause" else "Play",
                if (isPlaying) pausePendingIntent else playPendingIntent
            )
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        return builder.build()
    }
} 