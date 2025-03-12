package com.example.appbardemo.ui.theme

import ProgressDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R

class Mp3EditorActivity : AppCompatActivity() {
    private lateinit var waveformView: ImageView
    private lateinit var playButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var songTitleTextView: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var songUrl: String = ""
    private var songId: String = ""
    private var songName: String = ""
    private var songArtist: String = ""
    private var songDuration: Int = 0

    private var startTime: Int = 0
    private var endTime: Int = 0
    private var isPlaying = false

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBarRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3_editor)

        // Initialize views
        waveformView = findViewById(R.id.waveformView)
        playButton = findViewById(R.id.playButton)
        backButton = findViewById(R.id.backButton)
        songTitleTextView = findViewById(R.id.songTitleTextView) // Add this TextView to your layout

        // Get song details from intent
        songId = intent.getStringExtra("song_id") ?: ""
        songName = intent.getStringExtra("song_name") ?: "Unknown"
        songArtist = intent.getStringExtra("song_artist") ?: "Unknown"
        songUrl = intent.getStringExtra("song_url") ?: ""
        songDuration = intent.getIntExtra("song_duration", 0)

        // Set song title
        songTitleTextView.text = songName

        // Initial start and end time (entire song)
        startTime = 0
        endTime = songDuration

        // Setup click listeners
        setupClickListeners()

        // Initialize media player
        initializeMediaPlayer()

        // Generate waveform (in a real app, this would analyze the audio file)
        generateWaveform()

        // Initialize the seek bar updater
        setupSeekBarUpdater()
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            togglePlayback()
        }

        findViewById<ImageView>(R.id.zoomInButton).setOnClickListener {
            // Zoom in to the selected portion
            Toast.makeText(this, "Zooming in...", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.zoomOutButton).setOnClickListener {
            // Zoom out
            Toast.makeText(this, "Zooming out...", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.checkButton).setOnClickListener {
            // Save the cut audio
            saveAudioCut()
        }
    }

    private fun initializeMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(songUrl)
            mediaPlayer?.prepare()

            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(updateSeekBarRunnable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePlayback() {
        if (mediaPlayer == null) return

        if (isPlaying) {
            mediaPlayer?.pause()
            playButton.setImageResource(R.drawable.ic_play)
            handler.removeCallbacks(updateSeekBarRunnable)
        } else {
            // Set the start position and play
            mediaPlayer?.seekTo(startTime * 1000)
            mediaPlayer?.start()
            playButton.setImageResource(R.drawable.ic_pause)
            handler.postDelayed(updateSeekBarRunnable, 100)
        }

        isPlaying = !isPlaying
    }

    private fun setupSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer != null && isPlaying) {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0

                    // If reached the end of selection, stop playback
                    if (currentPosition >= endTime * 1000) {
                        mediaPlayer?.pause()
                        mediaPlayer?.seekTo(startTime * 1000)
                        isPlaying = false
                        playButton.setImageResource(R.drawable.ic_play)
                        return
                    }

                    handler.postDelayed(this, 100)
                }
            }
        }
    }

    private fun generateWaveform() {
        // In a real app, you would analyze the audio file to generate a waveform
        // For this example, we'll just use a placeholder image
        waveformView.setImageResource(R.drawable.wave_visualization)
    }

    private fun saveAudioCut() {
        // In a real app, you would use a library like FFmpeg to cut the audio file
        Toast.makeText(this, "Saving cut audio from ${formatTime(startTime)} to ${formatTime(endTime)}", Toast.LENGTH_SHORT).show()

        // Simulate processing time
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Processing audio...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()

            // Show success message
            val fileName = "${songName}_cut.mp3"
            Toast.makeText(this, "Audio saved as $fileName", Toast.LENGTH_LONG).show()

            // Return to previous screen
            finish()
        }, 2000)
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBarRunnable)
    }
}