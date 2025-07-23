package com.example.musicapp.presenter

import com.example.musicapp.data.model.Song
import com.example.musicapp.data.repository.SongRepository

interface MainView {
    fun showSongs(songs: List<Song>)
    fun showError(message: String)
}

class MainPresenter(private val view: MainView, private val repository: SongRepository) {
    fun loadSongs() {
        try {
            val songs = repository.getSongs()
            view.showSongs(songs)
        } catch (e: Exception) {
            view.showError(e.message ?: "Lỗi không xác định")
        }
    }
} 