package com.example.musicapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.data.model.Song
import com.example.musicapp.databinding.FragmentSongsBinding
import com.example.musicapp.presenter.LocalSongsPresenter
import com.example.musicapp.presenter.LocalSongsView

class LocalSongsFragment : Fragment(), LocalSongsView {
    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private var listener: OnSongSelectedListener? = null
    private lateinit var presenter: LocalSongsPresenter
    private var songs: List<Song> = emptyList()

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
        presenter = LocalSongsPresenter(this)
        binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        presenter.loadSongs()
    }

    override fun showSongs(songs: List<Song>) {
        this.songs = songs
        binding.rvSongs.adapter = SongAdapter(songs) { song ->
            val index = songs.indexOf(song)
            listener?.onSongSelected(song, songs, index)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        this.songs = emptyList()
        binding.rvSongs.adapter = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 