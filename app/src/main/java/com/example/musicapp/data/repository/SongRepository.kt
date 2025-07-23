package com.example.musicapp.data.repository

import com.example.musicapp.data.datasource.SongDataSource
import com.example.musicapp.data.model.Song

class SongRepository(
    private val local: SongDataSource,
    private val remote: SongDataSource
) {
    fun getSongs(): List<Song> {
        // Kết hợp cả local và remote
        return local.getSongs() + remote.getSongs()
    }
} 