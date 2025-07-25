package com.example.musicapp.presenter

import com.example.musicapp.data.datasource.LocalSongDataSource

class LocalSongsPresenter(private val view: LocalSongsView) {
    fun loadSongs() {
        val songs = LocalSongDataSource().getSongs()
        if (songs.isNotEmpty()) {
            view.showSongs(songs)
        } else {
            view.showError("Không có bài hát nào trong máy")
        }
    }
} 