package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class MusicTabFragment : Fragment() {

    private var tabType: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private var viewModel: MusicViewModel? = null
    private var progressBar: ProgressBar? = null

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

        // Try to get the shared ViewModel
        try {
            activity?.let {
                viewModel = ViewModelProvider(it)[MusicViewModel::class.java]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutRes = when (tabType) {
            "folder" -> R.layout.fragment_folder_tab
            else -> R.layout.fragment_music_tab
        }
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Initialize views
            recyclerView = view.findViewById(R.id.recyclerView)
            emptyText = view.findViewById(R.id.emptyText)
            progressBar = view.findViewById(R.id.progressBar)

            setupRecyclerView()

            // Only observe the ViewModel if it was properly initialized
            viewModel?.let { observeViewModel(it) }

        } catch (e: Exception) {
            e.printStackTrace()
            context?.let {
                Toast.makeText(it, "Error setting up view: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        when (tabType) {
            "album", "artist", "playlist" -> {
                recyclerView.layoutManager = GridLayoutManager(context, 2)
            }
            "folder" -> {
                recyclerView.layoutManager = LinearLayoutManager(context)
            }
            else -> {
                recyclerView.layoutManager = LinearLayoutManager(context)
            }
        }

        // Initially set up adapters with empty or mock data
        if (tabType == "folder") {
            // Use folder adapter with mock data
            val folderItems = createFolderItems()
            recyclerView.adapter = FolderAdapter(folderItems)
        } else {
            // For other tabs, use the music adapter with empty data initially
            recyclerView.adapter = MusicAdapter(emptyList(), tabType ?: "") { song, isFavorite ->
                // Handle favorite toggling
                viewModel?.updateFavoriteStatus(song.id, isFavorite)
            }
        }
    }

    private fun observeViewModel(viewModel: MusicViewModel) {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        // Observe songs data
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            if (tabType == "folder") {
                // Folders are handled separately
                return@observe
            }

            val filteredSongs = when (tabType) {
                "songs" -> songs
                "album" -> songs.distinctBy { it.album }.map {
                    it.copy(subtitle = "Album • ${songs.count { s -> s.album == it.album }} songs")
                }
                "artist" -> songs.distinctBy { it.artist }.map {
                    it.copy(subtitle = "${songs.count { s -> s.artist == it.artist }} songs")
                }
                "playlist" -> emptyList() // Handle playlists separately
                else -> emptyList()
            }

            if (filteredSongs.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyText.visibility = View.GONE

                // Update the adapter with new data
                (recyclerView.adapter as? MusicAdapter)?.updateData(filteredSongs)
            }
        }
    }

    private fun createFolderItems(): List<MusicItem> {
        return listOf(
            MusicItem("Folder 1", "8 items", "folder", R.drawable.ic_folder),
            MusicItem("Download", "15 items", "folder", R.drawable.ic_folder),
            MusicItem("Android", "5 items", "folder", R.drawable.ic_folder),
            MusicItem("Music", "32 items", "folder", R.drawable.ic_folder)
        )
    }
}