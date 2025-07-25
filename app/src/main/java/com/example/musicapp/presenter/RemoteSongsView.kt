package com.example.musicapp.presenter

import com.example.musicapp.data.model.Song

interface RemoteSongsView {
    fun showSongs(songs: List<Song>)
    fun showError(message: String)
} 