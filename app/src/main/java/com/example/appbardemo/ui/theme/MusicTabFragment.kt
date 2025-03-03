package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class MusicTabFragment : Fragment() {

    private var tabType: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: String): MusicTabFragment {
            val fragment = MusicTabFragment()
            val args = Bundle()
            args.putString(ARG_TAB_TYPE, tabType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabType = it.getString(ARG_TAB_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyText = view.findViewById(R.id.emptyText)

        setupRecyclerView()

        // Load data based on tab type
        loadData()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)

        // For folder view, use a different layout if needed
        when (tabType) {
            "album" -> {
                // Grid layout for albums
                recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            }
            "artist" -> {
                // Grid layout for artists
                recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            }
            else -> {
                // Linear layout for songs, folders, playlists
                recyclerView.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun loadData() {
        // In a real app, you would load data from a database or content provider
        // For this demo, we'll just create some sample data
        val items = when (tabType) {
            "album" -> createAlbumItems()
            "songs" -> createSongItems()
            "artist" -> createArtistItems()
            "folder" -> createFolderItems()
            "playlist" -> createPlaylistItems()
            else -> emptyList()
        }

        if (items.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            recyclerView.adapter = MusicAdapter(items, tabType ?: "")
        }
    }

    private fun createAlbumItems(): List<MusicItem> {
        // Sample album data
        return listOf(
            MusicItem("Senorita", "Shawn Mendes", "album", R.drawable.album),
            MusicItem("Blinding Lights", "The Weeknd", "album", R.drawable.album),
            MusicItem("Positions", "Ariana Grande", "album", R.drawable.album),
            MusicItem("After Hours", "The Weeknd", "album", R.drawable.album)
        )
    }

    private fun createSongItems(): List<MusicItem> {
        // Sample song data
        return listOf(
            MusicItem("Senorita", "Shawn Mendes", "song", R.drawable.album),
            MusicItem("Blinding Lights", "The Weeknd", "song", R.drawable.album),
            MusicItem("Positions", "Ariana Grande", "song", R.drawable.album),
            MusicItem("Save Your Tears", "The Weeknd", "song", R.drawable.album),
            MusicItem("Stay", "The Kid LAROI, Justin Bieber", "song", R.drawable.album),
            MusicItem("Good 4 U", "Olivia Rodrigo", "song", R.drawable.album),
            MusicItem("Levitating", "Dua Lipa", "song", R.drawable.album),
            MusicItem("Montero", "Lil Nas X", "song", R.drawable.album),
            MusicItem("Bad Habits", "Ed Sheeran", "song", R.drawable.album),
            MusicItem("Peaches", "Justin Bieber", "song", R.drawable.album)
        )
    }

    private fun createArtistItems(): List<MusicItem> {
        // Sample artist data
        return listOf(
            MusicItem("Alan Walker", "15 songs", "artist", R.drawable.artist),
            MusicItem("Ed Sheeran", "23 songs", "artist", R.drawable.artist),
            MusicItem("Ariana Grande", "18 songs", "artist", R.drawable.artist),
            MusicItem("The Weeknd", "12 songs", "artist", R.drawable.artist)
        )
    }

    private fun createFolderItems(): List<MusicItem> {
        // Sample folder data
        return listOf(
            MusicItem("Folder 1", "8 items", "folder", R.drawable.ic_folder),
            MusicItem("Download", "15 items", "folder", R.drawable.ic_folder),
            MusicItem("Android", "5 items", "folder", R.drawable.ic_folder),
            MusicItem("Music", "32 items", "folder", R.drawable.ic_folder)
        )
    }

    private fun createPlaylistItems(): List<MusicItem> {
        // Sample playlist data
        return listOf(
            MusicItem("My Favorites", "18 songs", "playlist", R.drawable.ic_playlist),
            MusicItem("Workout", "12 songs", "playlist", R.drawable.ic_playlist),
            MusicItem("Relaxing", "8 songs", "playlist", R.drawable.ic_playlist),
            MusicItem("Party", "20 songs", "playlist", R.drawable.ic_playlist)
        )
    }
}