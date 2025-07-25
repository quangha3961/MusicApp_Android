package com.example.musicapp.presenter

import com.example.musicapp.data.datasource.RemoteSongDataSource

class SearchPresenter(private val view: SearchView) {
    fun search(keyword: String) {
        val results = RemoteSongDataSource().searchSongs(keyword)
        if (results.isNotEmpty()) {
            view.showSongs(results)
        } else {
            view.showError("Không tìm thấy bài hát nào")
        }
    }
} 