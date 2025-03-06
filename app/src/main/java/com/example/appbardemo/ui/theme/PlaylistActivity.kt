package com.example.appbardemo.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class PlaylistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var playlistTitleTextView: TextView
    private lateinit var playlistInfoTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var playButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        // Initialize views
        recyclerView = findViewById(R.id.playlistSongsRecyclerView)
        playlistTitleTextView = findViewById(R.id.playlistTitleText)
        playlistInfoTextView = findViewById(R.id.playlistInfoText)
        backButton = findViewById(R.id.backButton)
        playButton = findViewById(R.id.playAllButton)

        // Get playlist info from intent
        val playlistTitle = intent.getStringExtra(EXTRA_PLAYLIST_TITLE) ?: "Playlist"
        val songsCount = intent.getIntExtra(EXTRA_SONGS_COUNT, 0)
        val playlistDuration = intent.getStringExtra(EXTRA_PLAYLIST_DURATION) ?: "0 Hours"

        // Set playlist info
        playlistTitleTextView.text = playlistTitle
        playlistInfoTextView.text = "$songsCount Songs • $playlistDuration"

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlaylistSongsAdapter(getMockSongs(), object : PlaylistSongsAdapter.OnSongClickListener {
            override fun onSongClick(song: Song, position: Int) {
                val intent = Intent(this@PlaylistActivity, PlaySongActivity::class.java).apply {
                    putExtra(PlaySongActivity.EXTRA_SONG_TITLE, song.title)
                    putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, song.artist)
                    putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, playlistTitle)
                    putExtra(PlaySongActivity.EXTRA_TRACK_NUMBER, position + 1)
                    putExtra(PlaySongActivity.EXTRA_TOTAL_TRACKS, songsCount)
                    putExtra(PlaySongActivity.EXTRA_DURATION, song.durationInSeconds)
                    putExtra(PlaySongActivity.EXTRA_IMAGE_URL , R.drawable.album)
                }
                startActivity(intent)
            }
        })

        // Set up click listeners
        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            // Play all songs in the playlist, starting with the first one
            val firstSong = getMockSongs().firstOrNull() ?: return@setOnClickListener
            val intent = Intent(this, PlaySongActivity::class.java).apply {
                putExtra(PlaySongActivity.EXTRA_SONG_TITLE, firstSong.title)
                putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, firstSong.artist)
                putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, playlistTitle)
                putExtra(PlaySongActivity.EXTRA_TRACK_NUMBER, 1)
                putExtra(PlaySongActivity.EXTRA_TOTAL_TRACKS, songsCount)
                putExtra(PlaySongActivity.EXTRA_DURATION, firstSong.durationInSeconds)
                putExtra(PlaySongActivity.EXTRA_IMAGE_URL , R.drawable.album)
            }
            startActivity(intent)
        }
    }

    // Mock data for songs in the playlist
    private fun getMockSongs(): List<Song> {
        return listOf(
            Song("Low Earth Orbit", "A Synthwave Mix", 210, true),
            Song("Low Earth Orbit", "A Synthwave Mix", 185),
            Song("Low Earth Orbit", "A Synthwave Mix", 195, true),
            Song("Low Earth Orbit", "A Synthwave Mix", 240),
            Song("Low Earth Orbit", "A Synthwave Mix", 220, true),
            Song("Low Earth Orbit", "A Synthwave Mix", 200),
            Song("Low Earth Orbit", "A Synthwave Mix", 180),
            Song("Low Earth Orbit", "A Synthwave Mix", 225)
        )
    }

    companion object {
        const val EXTRA_PLAYLIST_TITLE = "playlist_title"
        const val EXTRA_SONGS_COUNT = "songs_count"
        const val EXTRA_PLAYLIST_DURATION = "playlist_duration"
    }
}

data class Song(
    val title: String,
    val artist: String,
    val durationInSeconds: Int,
    val isFavorite: Boolean = false
)