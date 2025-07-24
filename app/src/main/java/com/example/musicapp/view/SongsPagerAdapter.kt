package com.example.musicapp.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SongsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LocalSongsFragment()
            1 -> RemoteSongsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
} 