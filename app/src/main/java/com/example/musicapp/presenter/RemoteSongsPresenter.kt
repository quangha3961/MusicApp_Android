package com.example.musicapp.presenter

import com.example.musicapp.data.datasource.RemoteSongDataSource

class RemoteSongsPresenter(private val view: RemoteSongsView) {
    fun loadSongs() {
        val songs = RemoteSongDataSource().getSongs()
        if (songs.isNotEmpty()) {
            view.showSongs(songs)
        } else {
            view.showError("Không có bài hát online nào")
        }
    }
} 