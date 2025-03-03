package com.example.appbardemo.ui.theme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MusicPagerAdapter(activity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        // Create different fragments based on position
        return when (position) {
            0 -> MusicTabFragment.newInstance("album")
            1 -> MusicTabFragment.newInstance("songs")
            2 -> MusicTabFragment.newInstance("artist")
            3 -> MusicTabFragment.newInstance("folder")
            4 -> MusicTabFragment.newInstance("playlist")
            else -> MusicTabFragment.newInstance("songs")
        }
    }
}