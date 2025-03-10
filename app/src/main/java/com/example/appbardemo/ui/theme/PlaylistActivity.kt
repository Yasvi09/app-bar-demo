package com.example.appbardemo.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appbardemo.R
import com.google.android.material.imageview.ShapeableImageView

class PlaylistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var playlistTitleTextView: TextView
    private lateinit var playlistInfoTextView: TextView
    private lateinit var playlistImageView: ImageView
    private lateinit var backButton: ImageView
    private lateinit var playButton: ShapeableImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: MusicViewModel
    private lateinit var adapter: PlaylistSongsAdapter

    private var playlistId: String = ""
    private var currentPlaylist: PlaylistModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        initializeViews()

        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID) ?: ""
        val playlistTitle = intent.getStringExtra(EXTRA_PLAYLIST_TITLE) ?: "Playlist"

        playlistTitleTextView.text = playlistTitle
        playlistInfoTextView.text = "Loading songs..."

        progressBar.visibility = View.VISIBLE

        setupRecyclerView()

        setupClickListeners()

        loadPlaylistData()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.playlistSongsRecyclerView)
        playlistTitleTextView = findViewById(R.id.playlistTitleText)
        playlistInfoTextView = findViewById(R.id.playlistInfoText)
        playlistImageView = findViewById(R.id.playlistImageView)
        backButton = findViewById(R.id.backButton)
        playButton = findViewById(R.id.playAllButton)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistSongsAdapter(emptyList(), object : PlaylistSongsAdapter.OnSongClickListener {
            override fun onSongClick(song: SongModel, position: Int) {
                playSong(song, position)
            }
        })
        recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {

            val songs = adapter.getSongs()
            if (songs.isNotEmpty()) {
                playSong(songs[0], 0)
            } else {
                Toast.makeText(this, "No songs in this playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPlaylistData() {
        if (playlistId.isEmpty()) {
            Toast.makeText(this, "Invalid playlist ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get the playlist details
        viewModel.getPlaylist(playlistId) { playlist ->
            if (playlist == null) {
                Toast.makeText(this, "Playlist not found", Toast.LENGTH_SHORT).show()
                finish()
                return@getPlaylist
            }

            // Update current playlist
            currentPlaylist = playlist

            // Update UI with playlist details
            runOnUiThread {
                playlistTitleTextView.text = playlist.title

                // Load playlist cover image
                if (playlist.coverImageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(playlist.coverImageUrl)
                        .apply(RequestOptions().placeholder(R.drawable.album))
                        .into(playlistImageView)
                }
            }

            // Load the songs in this playlist
            loadPlaylistSongs(playlist)
        }
    }

    private fun loadPlaylistSongs(playlist: PlaylistModel) {
        viewModel.loadPlaylistSongs(playlist)

        // Observe songs in the playlist
        viewModel.playlistSongs.observe(this) { songs ->
            progressBar.visibility = View.GONE

            if (songs.isEmpty()) {
                playlistInfoTextView.text = "0 Songs • 0 Minutes"
                Toast.makeText(this, "No songs in this playlist", Toast.LENGTH_SHORT).show()
            } else {
                // Calculate total duration
                val totalDuration = songs.sumOf { it.duration }
                val hours = totalDuration / 3600
                val minutes = (totalDuration % 3600) / 60

                val durationText = when {
                    hours > 0 -> "$hours:${minutes.toString().padStart(2, '0')} Hours"
                    else -> "$minutes Minutes"
                }

                playlistInfoTextView.text = "${songs.size} Songs • $durationText"
            }

            // Update the adapter with songs
            adapter.updateSongs(songs)
        }

        // Observe errors
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                progressBar.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun playSong(song: SongModel, position: Int) {
        val songs = adapter.getSongs()
        val intent = Intent(this, PlaySongActivity::class.java).apply {
            putExtra(PlaySongActivity.EXTRA_SONG_TITLE, song.name)
            putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, song.artist)
            putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, currentPlaylist?.title ?: "Playlist")
            putExtra(PlaySongActivity.EXTRA_TRACK_NUMBER, position + 1)
            putExtra(PlaySongActivity.EXTRA_TOTAL_TRACKS, songs.size)
            putExtra(PlaySongActivity.EXTRA_DURATION, song.duration)
            putExtra(PlaySongActivity.EXTRA_SONG_ID, song.id)
            putExtra(PlaySongActivity.EXTRA_AUDIO_URL, song.url)
            putExtra(PlaySongActivity.EXTRA_IMAGE_URL, song.image)
            // Also pass the playlist ID for context
            putExtra(PlaySongActivity.EXTRA_PLAYLIST_ID, playlistId)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "playlist_id"
        const val EXTRA_PLAYLIST_TITLE = "playlist_title"
        const val EXTRA_SONGS_COUNT = "songs_count"
        const val EXTRA_PLAYLIST_DURATION = "playlist_duration"
    }
}