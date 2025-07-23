package com.example.musicapp.data.model

// Data class đại diện cho một bài hát

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val url: String, // Đường dẫn file local hoặc link online
    val imageUrl: String? = null
) 