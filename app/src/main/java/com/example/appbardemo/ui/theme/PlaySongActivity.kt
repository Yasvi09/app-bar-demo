package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R
import com.google.android.material.imageview.ShapeableImageView
import java.util.concurrent.TimeUnit

class PlaySongActivity : AppCompatActivity() {

    private lateinit var albumArt: ShapeableImageView
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var sourceText: TextView
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var trackPositionText: TextView
    private lateinit var songSeekBar: SeekBar
    private lateinit var playPauseButton: ImageView
    private lateinit var favoriteButton: ImageView

    private var isPlaying = false
    private var isFavorite = false
    private var currentPosition = 0
    private var totalDuration = 360 // 6 minutes in seconds (default)

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBarRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_song)

        initializeViews()
        setupClickListeners()
        getSongDetails()
        setupSeekBarUpdater()
    }

    private fun initializeViews() {
        albumArt = findViewById(R.id.albumArt)
        songTitle = findViewById(R.id.songTitle)
        artistName = findViewById(R.id.artistName)
        sourceText = findViewById(R.id.sourceText)
        currentTimeText = findViewById(R.id.currentTimeText)
        totalTimeText = findViewById(R.id.totalTimeText)
        trackPositionText = findViewById(R.id.trackPositionText)
        songSeekBar = findViewById(R.id.songSeekBar)
        playPauseButton = findViewById(R.id.playPauseButton)
        favoriteButton = findViewById(R.id.favoriteButton)

        // Set up seekbar
        songSeekBar.max = totalDuration
        songSeekBar.progress = currentPosition
        updateTimeTexts()

        // Set initial play/pause button state
        updatePlayPauseButton()
    }

    private fun setupClickListeners() {
        // Back button click listener
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Menu button click listener
        findViewById<ImageView>(R.id.menuButton).setOnClickListener {
            // Show options menu
        }

        // Play/Pause button click listener
        findViewById<View>(R.id.playPauseContainer).setOnClickListener {
            togglePlayPause()
        }

        // Previous button click listener
        findViewById<ImageView>(R.id.previousButton).setOnClickListener {
            playPreviousSong()
        }

        // Next button click listener
        findViewById<ImageView>(R.id.nextButton).setOnClickListener {
            playNextSong()
        }

        // Favorite button click listener
        favoriteButton.setOnClickListener {
            toggleFavorite()
        }

        // Set up seekbar change listener
        songSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentPosition = progress
                    updateTimeTexts()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Pause updates while user is dragging
                handler.removeCallbacks(updateSeekBarRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Resume updates when user stops dragging
                if (isPlaying) {
                    handler.postDelayed(updateSeekBarRunnable, 1000)
                }
            }
        })
    }

    private fun getSongDetails() {
        // Start in playing state
        isPlaying = true

        // Get data from intent extras
        val title = intent.getStringExtra(EXTRA_SONG_TITLE) ?: "Unknown Title"
        val artist = intent.getStringExtra(EXTRA_ARTIST_NAME) ?: "Unknown Artist"
        val albumName = intent.getStringExtra(EXTRA_ALBUM_NAME) ?: "Unknown Album"
        val trackNumber = intent.getIntExtra(EXTRA_TRACK_NUMBER, 1)
        val totalTracks = intent.getIntExtra(EXTRA_TOTAL_TRACKS, 10)
        val duration = intent.getIntExtra(EXTRA_DURATION, 360) // In seconds
        val imageResId = intent.getIntExtra(EXTRA_IMAGE_RES_ID, R.drawable.album)

        // Update UI with song details
        songTitle.text = title
        artistName.text = artist
        sourceText.text = albumName
        trackPositionText.text = "$trackNumber/$totalTracks"
        totalDuration = duration
        songSeekBar.max = totalDuration
        albumArt.setImageResource(imageResId)

        // Format total duration and update text
        totalTimeText.text = formatTime(totalDuration)
    }

    private fun setupSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (isPlaying && currentPosition < totalDuration) {
                    currentPosition += 1
                    songSeekBar.progress = currentPosition
                    updateTimeTexts()
                    handler.postDelayed(this, 1000)
                } else if (currentPosition >= totalDuration) {
                    // Song finished, play next song
                    playNextSong()
                    isPlaying = true
                    updatePlayPauseButton()
                }
            }
        }
    }

    private fun updateTimeTexts() {
        currentTimeText.text = formatTime(currentPosition)
        totalTimeText.text = formatTime(totalDuration)
    }

    private fun formatTime(seconds: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying

        if (isPlaying) {
            // Start or resume playback
            handler.postDelayed(updateSeekBarRunnable, 1000)
        } else {
            // Pause playback
            handler.removeCallbacks(updateSeekBarRunnable)
        }

        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite

        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun playPreviousSong() {
        // Reset to start of song if we're past 3 seconds, otherwise go to previous song
        if (currentPosition > 3) {
            currentPosition = 0
            songSeekBar.progress = 0
            updateTimeTexts()
        } else {
            // In a real app, you would load and play the previous song here
            // For this demo, we'll just reset the current song
            currentPosition = 0
            songSeekBar.progress = 0
            updateTimeTexts()
        }
    }

    private fun playNextSong() {
        // In a real app, you would load and play the next song here
        // For this demo, we'll just reset the current song
        currentPosition = 0
        songSeekBar.progress = 0
        updateTimeTexts()
    }

    override fun onStart() {
        super.onStart()
        // Automatically start playing when activity starts
        if (isPlaying) {
            handler.postDelayed(updateSeekBarRunnable, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPlaying) {
            handler.postDelayed(updateSeekBarRunnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    companion object {
        const val EXTRA_SONG_TITLE = "song_title"
        const val EXTRA_ARTIST_NAME = "artist_name"
        const val EXTRA_ALBUM_NAME = "album_name"
        const val EXTRA_TRACK_NUMBER = "track_number"
        const val EXTRA_TOTAL_TRACKS = "total_tracks"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_IMAGE_RES_ID = "image_res_id"
        const val EXTRA_SONG_ID = "song_id"
    }
}