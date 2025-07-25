package com.example.musicapp.view

import com.example.musicapp.data.model.Song

interface OnSongSelectedListener {
    fun onSongSelected(song: Song, list: List<Song>, index: Int)
} 