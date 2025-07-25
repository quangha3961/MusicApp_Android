package com.example.musicapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.view.LocalSongsFragment
import com.example.musicapp.view.RemoteSongsFragment
import com.example.musicapp.view.SongsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), LocalSongsFragment.OnSongSelectedListener, RemoteSongsFragment.OnSongSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: Song? = null
    private var songList: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = SongsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Local" else "Remote"
        }.attach()

        // Ẩn mini player ban đầu
        binding.miniPlayer.root.visibility = View.GONE

        // Xử lý nút play/pause
        binding.miniPlayer.btnPlayPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer?.start()
                binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
        // Xử lý next
        binding.miniPlayer.btnNext.setOnClickListener { playNext() }
        // Xử lý prev
        binding.miniPlayer.btnPrev.setOnClickListener { playPrev() }

        // SeekBar
        binding.miniPlayer.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer != null && !isUserSeeking) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                mediaPlayer?.seekTo(seekBar?.progress ?: 0)
            }
        })
    }

    // Callback khi chọn bài hát từ fragment
    override fun onSongSelected(song: Song, list: List<Song>, index: Int) {
        songList = list
        currentIndex = index
        playSong(song)
    }

    private fun playSong(song: Song) {
        currentSong = song
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            if (song.url.startsWith("android.resource://")) {
                mediaPlayer?.setDataSource(this, Uri.parse(song.url))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                updateMiniPlayer(song)
                binding.miniPlayer.seekBar.max = mediaPlayer?.duration ?: 100
            } else {
                mediaPlayer?.setDataSource(song.url)
                mediaPlayer?.setOnPreparedListener {
                    it.start()
                    updateMiniPlayer(song)
                    binding.miniPlayer.seekBar.max = it.duration
                }
                mediaPlayer?.prepareAsync()
            }
            binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            binding.miniPlayer.root.visibility = View.VISIBLE
            mediaPlayer?.setOnCompletionListener { playNext() }
            handler.post(updateSeekBarRunnable)
        } catch (e: Exception) {
            Toast.makeText(this, "Không phát được nhạc: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMiniPlayer(song: Song) {
        binding.miniPlayer.tvSongTitle.text = song.title
        Glide.with(this)
            .load(song.imageUrl)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(binding.miniPlayer.imgSongThumb)
        binding.miniPlayer.seekBar.progress = 0
        // Không set max ở đây!
    }

    private fun playNext() {
        if (songList.isNotEmpty() && currentIndex < songList.size - 1) {
            currentIndex++
            playSong(songList[currentIndex])
        }
    }

    private fun playPrev() {
        if (songList.isNotEmpty() && currentIndex > 0) {
            currentIndex--
            playSong(songList[currentIndex])
        }
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && !isUserSeeking) {
                binding.miniPlayer.seekBar.progress = mediaPlayer?.currentPosition ?: 0
            }
            handler.postDelayed(this, 500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBarRunnable)
    }
}