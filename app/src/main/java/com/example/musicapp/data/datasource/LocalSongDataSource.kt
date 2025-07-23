package com.example.musicapp.data.datasource

import com.example.musicapp.data.model.Song

class LocalSongDataSource : SongDataSource {
    override fun getSongs(): List<Song> {
        // Sử dụng resource id cho nhạc trong res/raw
        return listOf(
            Song("1", "Escape (The Pina Colada Song)", "Rupert Holmes", "android.resource://com.example.musicapp/raw/escape", null),
            Song("2", "September", "Earth, Wind & Fire", "android.resource://com.example.musicapp/raw/september", null),
            Song("3", "All Night", "Sidy Ranks", "android.resource://com.example.musicapp/raw/allnight", null)
        )
    }
} 