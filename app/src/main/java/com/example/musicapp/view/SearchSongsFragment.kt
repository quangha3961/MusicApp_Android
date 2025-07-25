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
import com.example.musicapp.databinding.FragmentSearchSongsBinding
import com.example.musicapp.presenter.SearchPresenter
import com.example.musicapp.presenter.SearchView

class SearchSongsFragment : Fragment(), SearchView {
    private var _binding: FragmentSearchSongsBinding? = null
    private val binding get() = _binding!!
    private var listener: OnSongSelectedListener? = null
    private var searchResults: List<Song> = emptyList()
    private lateinit var presenter: SearchPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SearchPresenter(this)
        binding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        binding.btnSearch.setOnClickListener {
            val keyword = binding.edtSearch.text.toString().trim()
            if (keyword.isEmpty()) {
                Toast.makeText(requireContext(), "Nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            presenter.search(keyword)
        }
    }

    override fun showSongs(songs: List<Song>) {
        searchResults = songs
        binding.rvSongs.adapter = SongAdapter(songs) { song ->
            val index = songs.indexOf(song)
            listener?.onSongSelected(song, songs, index)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        searchResults = emptyList()
        binding.rvSongs.adapter = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 