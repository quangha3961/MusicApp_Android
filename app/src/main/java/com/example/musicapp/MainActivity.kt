package com.example.musicapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.datasource.LocalSongDataSource
import com.example.musicapp.data.datasource.RemoteSongDataSource
import com.example.musicapp.data.repository.SongRepository
import com.example.musicapp.data.model.Song
import com.example.musicapp.presenter.MainPresenter
import com.example.musicapp.presenter.MainView
import com.example.musicapp.view.SongAdapter
import com.example.musicapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainView {
    private lateinit var presenter: MainPresenter
    private lateinit var adapter: SongAdapter
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(
            this,
            SongRepository(LocalSongDataSource(), RemoteSongDataSource())
        )

        binding.rvSongs.layoutManager = LinearLayoutManager(this)
        adapter = SongAdapter(listOf()) { song ->
            playSong(song)
        }
        binding.rvSongs.adapter = adapter

        presenter.loadSongs()
    }

    override fun showSongs(songs: List<Song>) {
        adapter = SongAdapter(songs) { song ->
            playSong(song)
        }
        binding.rvSongs.adapter = adapter
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun playSong(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            if (song.url.startsWith("android.resource://")) {
                mediaPlayer?.setDataSource(this, Uri.parse(song.url))
            } else {
                mediaPlayer?.setDataSource(song.url)
            }
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            Toast.makeText(this, "Đang phát: ${song.title}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Không phát được nhạc: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}