package com.example.musicapp.view

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songs = LocalSongDataSource().getSongs()
        binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSongs.adapter = com.example.musicapp.view.SongAdapter(songs) { song ->
            playSong(song)
        }
    }

    private fun playSong(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            if (song.url.startsWith("android.resource://")) {
                mediaPlayer?.setDataSource(requireContext(), Uri.parse(song.url))
            } else {
                mediaPlayer?.setDataSource(song.url)
            }
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            Toast.makeText(requireContext(), "Đang phát: ${song.title}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Không phát được nhạc: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
} 