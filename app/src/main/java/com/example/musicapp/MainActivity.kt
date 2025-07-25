package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.musicapp.data.model.Song
import com.example.musicapp.service.MusicService
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.view.LocalSongsFragment
import com.example.musicapp.view.RemoteSongsFragment
import com.example.musicapp.view.SongsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), LocalSongsFragment.OnSongSelectedListener, RemoteSongsFragment.OnSongSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var currentSong: Song? = null
    private var songList: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false

    private val musicStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MusicService.BROADCAST_STATE) {
                val state = intent.getStringExtra(MusicService.EXTRA_STATE)
                val song = intent.getSerializableExtra(MusicService.EXTRA_SONG) as? Song
                val index = intent.getIntExtra(MusicService.EXTRA_INDEX, -1)
                val list = intent.getSerializableExtra(MusicService.EXTRA_SONG_LIST) as? List<Song>
                if (song != null && list != null && index != -1) {
                    currentSong = song
                    songList = list
                    currentIndex = index
                    updateMiniPlayer(song)
                    binding.miniPlayer.root.visibility = View.VISIBLE
                    if (state == MusicService.STATE_PLAYING) {
                        binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                        binding.miniPlayer.btnPlayPause.tag = "playing"
                    } else {
                        binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                        binding.miniPlayer.btnPlayPause.tag = "paused"
                    }
                }
            }
        }
    }

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MusicService.BROADCAST_PROGRESS) {
                val progress = intent.getIntExtra(MusicService.EXTRA_PROGRESS, 0)
                val duration = intent.getIntExtra(MusicService.EXTRA_DURATION, 100)
                if (!isUserSeeking) {
                    binding.miniPlayer.seekBar.max = duration
                    binding.miniPlayer.seekBar.progress = progress
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = SongsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Local" else "Remote"
        }.attach()

        // Đăng ký receiver nhận trạng thái nhạc
        LocalBroadcastManager.getInstance(this).registerReceiver(
            musicStateReceiver, IntentFilter(MusicService.BROADCAST_STATE)
        )
        // Đăng ký receiver nhận tiến trình nhạc
        LocalBroadcastManager.getInstance(this).registerReceiver(
            progressReceiver, IntentFilter(MusicService.BROADCAST_PROGRESS)
        )

        // Ẩn mini player ban đầu
        binding.miniPlayer.root.visibility = View.GONE

        // Xử lý nút play/pause
        binding.miniPlayer.btnPlayPause.setOnClickListener {
            if (binding.miniPlayer.btnPlayPause.tag == "playing") {
                sendServiceIntent(MusicService.ACTION_PAUSE)
                binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                binding.miniPlayer.btnPlayPause.tag = "paused"
            } else {
                sendServiceIntent(MusicService.ACTION_PLAY)
                binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                binding.miniPlayer.btnPlayPause.tag = "playing"
            }
        }
        // Xử lý next
        binding.miniPlayer.btnNext.setOnClickListener { sendServiceIntent(MusicService.ACTION_NEXT) }
        // Xử lý prev
        binding.miniPlayer.btnPrev.setOnClickListener { sendServiceIntent(MusicService.ACTION_PREV) }

        // SeekBar: gửi seek về Service khi người dùng tua
        binding.miniPlayer.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                val progress = seekBar?.progress ?: 0
                sendSeekToService(progress)
            }
        })
    }

    // Callback khi chọn bài hát từ fragment
    override fun onSongSelected(song: Song, list: List<Song>, index: Int) {
        songList = list
        currentIndex = index
        currentSong = song
        updateMiniPlayer(song)
        binding.miniPlayer.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        binding.miniPlayer.btnPlayPause.tag = "playing"
        binding.miniPlayer.root.visibility = View.VISIBLE
        sendServiceIntent(MusicService.ACTION_PLAY, song, list, index)
    }

    private fun updateMiniPlayer(song: Song) {
        binding.miniPlayer.tvSongTitle.text = song.title
        Glide.with(this)
            .load(song.imageUrl)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(binding.miniPlayer.imgSongThumb)
        binding.miniPlayer.seekBar.progress = 0
    }

    private fun sendServiceIntent(action: String, song: Song? = currentSong, list: List<Song>? = songList, index: Int = currentIndex) {
        val intent = Intent(this, MusicService::class.java)
        intent.action = action
        if (action == MusicService.ACTION_PLAY && song != null && list != null && index != -1) {
            intent.putExtra(MusicService.EXTRA_SONG, song)
            intent.putExtra(MusicService.EXTRA_SONG_LIST, ArrayList(list))
            intent.putExtra(MusicService.EXTRA_INDEX, index)
        }
        startService(intent)
    }

    private fun sendSeekToService(progress: Int) {
        // Gửi intent custom tới Service để tua nhạc
        val intent = Intent(this, MusicService::class.java)
        intent.action = "com.example.musicapp.SEEK_TO"
        intent.putExtra("seek_to", progress)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicStateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver)
    }
}