package com.example.musicapp.view

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.datasource.LocalSongDataSource
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentSongsBinding

class LocalSongsFragment : Fragment() {
    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private var listener: OnSongSelectedListener? = null
    private lateinit var songs: List<Song>

    interface OnSongSelectedListener {
        fun onSongSelected(song: Song, list: List<Song>, index: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songs = LocalSongDataSource().getSongs()
        binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSongs.adapter = com.example.musicapp.view.SongAdapter(songs) { song ->
            val index = songs.indexOf(song)
            listener?.onSongSelected(song, songs, index)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
} 