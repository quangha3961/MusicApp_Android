package com.example.musicapp.data.datasource

import com.example.musicapp.data.model.Song
 
interface SongDataSource {
    fun getSongs(): List<Song>
} 