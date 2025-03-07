package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class PlaylistTabFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private lateinit var viewModel: MusicViewModel

    companion object {
        fun newInstance(): PlaylistTabFragment {
            return PlaylistTabFragment()
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

        try {
            // Initialize views
            recyclerView = view.findViewById(R.id.recyclerView)
            emptyText = view.findViewById(R.id.emptyText)
            progressBar = view.findViewById(R.id.progressBar)

            // Get shared ViewModel
            viewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]

            // Setup RecyclerView with Grid layout
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            playlistsAdapter = PlaylistsAdapter(emptyList())
            recyclerView.adapter = playlistsAdapter

            // Observe ViewModel data
            observeViewModel()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeViewModel() {
        // Observe playlists
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
                emptyText.text = "No playlists found"
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyText.visibility = View.GONE
                playlistsAdapter.updateData(playlists)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh playlists data when returning to this fragment
        viewModel.loadPlaylists()
    }
}