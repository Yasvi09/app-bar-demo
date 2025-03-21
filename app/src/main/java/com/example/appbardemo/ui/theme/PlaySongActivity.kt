package com.example.appbardemo.ui.theme

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appbardemo.R
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import com.google.android.material.imageview.ShapeableImageView
import java.util.concurrent.TimeUnit

class PlaySongActivity : AppCompatActivity(), Player.Listener {

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
    private lateinit var fabLayout: FrameLayout
    private lateinit var themeManager: ThemeManager

    private var player: ExoPlayer? = null
    private var isPlaying = false
    private var isFavorite = false
    private var currentPosition = 0
    private var totalDuration = 0
    private var songId = ""
    private var audioUrl = ""
    private var imageUrl = ""
    private var playlistId: String? = null
    private var currentSongIndex: Int = 0
    private var playlistSongs: List<SongModel> = emptyList()
    private var songIdList: ArrayList<String>? = null
    private var sourceType: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBarRunnable: Runnable

    private lateinit var viewModel: MusicViewModel


    // Modify the onCreate method to get the song ID list and source type
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_song)

        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        init()
        setupSeekBarUpdater()
        setupClickListeners()

        // Get the source type and song ID list if coming from songs tab
        sourceType = intent.getStringExtra(EXTRA_SOURCE_TYPE) ?: ""
        songIdList = intent.getStringArrayListExtra(EXTRA_SONG_ID_LIST)

        getSongDetails()  // First get the song details including audioUrl
        setupPlayer()     // Then set up the player with the populated audioUrl

        // Get the playlist ID if coming from a playlist
        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID)

        // If we have a playlist ID, load the playlist songs
        if (!playlistId.isNullOrEmpty()) {
            loadPlaylistSongs()
        } else if (sourceType == "songs_tab" && !songIdList.isNullOrEmpty()) {
            // If coming from songs tab, load these songs
            loadSongsFromIdList()
        }

        val seekBar = findViewById<SeekBar>(R.id.songSeekBar)
        val themeColor = ThemeManager.getInstance(this).getThemeColor()
        seekBar.progressTintList = ColorStateList.valueOf(themeColor)
        seekBar.thumbTintList = ColorStateList.valueOf(themeColor)
    }

    private fun loadSongsFromIdList() {
        val songIds = songIdList ?: return

        // Find current song index in the list
        currentSongIndex = songIds.indexOf(songId)

        viewModel.getSongsByIds(songIds) { fetchedSongs ->
            playlistSongs = fetchedSongs
            updateTrackPositionText()
        }
    }

    private fun init() {
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

        fabLayout = findViewById(R.id.playPauseContainer)

        // Apply theme color to FAB background
themeManager = ThemeManager.getInstance(this)
        themeManager.applyThemeToFabGradient(fabLayout)


        songSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentPosition = progress
                    updateTimeTexts()
                    player?.seekTo(progress * 1000L)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateSeekBarRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isPlaying) {
                    handler.postDelayed(updateSeekBarRunnable, 1000)
                }
            }
        })
    }

    private fun setupClickListeners() {

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
            skipToPrevious()
        }

        // Equalizer button click listener
        findViewById<ImageView>(R.id.equalizerButton).setOnClickListener {
            openEqualizer()
        }

        // Next button click listener
        findViewById<ImageView>(R.id.nextButton).setOnClickListener {
            skipToNext()
        }

        favoriteButton.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun getSongDetails() {
        // Get data from intent extras
        val title = intent.getStringExtra(EXTRA_SONG_TITLE) ?: "Unknown Title"
        val artist = intent.getStringExtra(EXTRA_ARTIST_NAME) ?: "Unknown Artist"
        val albumName = intent.getStringExtra(EXTRA_ALBUM_NAME) ?: "Unknown Album"
        val trackNumber = intent.getIntExtra(EXTRA_TRACK_NUMBER, 1)
        val totalTracks = intent.getIntExtra(EXTRA_TOTAL_TRACKS, 10)
        totalDuration = intent.getIntExtra(EXTRA_DURATION, 360) // In seconds
        songId = intent.getStringExtra(EXTRA_SONG_ID) ?: ""
        audioUrl = intent.getStringExtra(EXTRA_AUDIO_URL) ?: ""
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL) ?: ""

        // Update UI with song details
        songTitle.text = title
        artistName.text = artist
        sourceText.text = albumName
        trackPositionText.text = "$trackNumber/$totalTracks"
        songSeekBar.max = totalDuration
        songSeekBar.progress = 0

        // Load album art using Glide
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions().placeholder(R.drawable.album).centerCrop())
                .into(albumArt)
        } else {
            albumArt.setImageResource(R.drawable.album)
        }

        // Format total duration and update text
        totalTimeText.text = formatTime(totalDuration)
        currentTimeText.text = formatTime(0)
    }

    @OptIn(UnstableApi::class)
    private fun setupPlayer() {
        player = SimpleExoPlayer.Builder(this).build()

        player?.addListener(this)

        if (audioUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
            player?.setMediaItem(mediaItem)
            player?.prepare()

            // Auto-start playback
            player?.play()
            isPlaying = true
            updatePlayPauseButton()
            handler.postDelayed(updateSeekBarRunnable, 1000)
        } else {
            Toast.makeText(this, "Error: Audio URL is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (player != null && isPlaying) {
                    // Get current position in seconds
                    currentPosition = (player?.currentPosition ?: 0).toInt() / 1000
                    songSeekBar.progress = currentPosition
                    updateTimeTexts()
                    handler.postDelayed(this, 1000)
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
        if (player == null) return

        isPlaying = !isPlaying

        if (isPlaying) {
            player?.play()
            handler.postDelayed(updateSeekBarRunnable, 1000)
        } else {
            player?.pause()
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
        if (songId.isEmpty()) return

        isFavorite = !isFavorite

        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }

        viewModel.updateFavoriteStatus(songId, isFavorite)
    }

    private fun skipToPrevious() {
        if (player == null) return

        // Reset to start of song if we're past 3 seconds, otherwise go to previous song
        if ((playlistSongs.isNotEmpty() || !songIdList.isNullOrEmpty()) &&
            currentSongIndex > 0 && currentPosition <= 3) {
            // Go to previous song
            currentSongIndex--
            playSongFromIndex(currentSongIndex)
        } else {
            // Just reset the current song
            player?.seekTo(0)
            currentPosition = 0
            songSeekBar.progress = 0
            updateTimeTexts()
        }
    }

    private fun skipToNext() {
        if ((playlistSongs.isNotEmpty() || !songIdList.isNullOrEmpty()) &&
            currentSongIndex < playlistSongs.size - 1) {
            // Go to next song
            currentSongIndex++
            playSongFromIndex(currentSongIndex)
        } else {
            // Loop back to the first song if at the end
            if (playlistSongs.isNotEmpty() || !songIdList.isNullOrEmpty()) {
                currentSongIndex = 0
                playSongFromIndex(currentSongIndex)
            } else {
                // If no more songs, just reset current song
                player?.seekTo(0)
                currentPosition = 0
                songSeekBar.progress = 0
                updateTimeTexts()
            }
        }
    }

    private fun playSongFromIndex(index: Int) {
        if (index < 0 || index >= playlistSongs.size) return

        val song = playlistSongs[index]

        songTitle.text = song.name
        artistName.text = song.artist
        totalDuration = song.duration
        songSeekBar.max = totalDuration
        songSeekBar.progress = 0

        audioUrl = song.url
        songId = song.id

        player?.stop()
        player?.clearMediaItems()

        if (audioUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            isPlaying = true
            updatePlayPauseButton()
        }

        if (song.image.isNotEmpty()) {
            Glide.with(this)
                .load(song.image)
                .apply(RequestOptions().placeholder(R.drawable.album).centerCrop())
                .into(albumArt)
        }

        updateTrackPositionText()
    }



    private fun playSongFromPlaylist(index: Int) {
        if (index < 0 || index >= playlistSongs.size) return

        val song = playlistSongs[index]

        songTitle.text = song.name
        artistName.text = song.artist
        totalDuration = song.duration
        songSeekBar.max = totalDuration
        songSeekBar.progress = 0

        audioUrl = song.url
        player?.stop()
        player?.clearMediaItems()

        if (audioUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(Uri.parse(audioUrl))
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            isPlaying = true
            updatePlayPauseButton()
        }

        if (song.image.isNotEmpty()) {
            Glide.with(this)
                .load(song.image)
                .apply(RequestOptions().placeholder(R.drawable.album).centerCrop())
                .into(albumArt)
        }

        updateTrackPositionText()
    }

    @OptIn(UnstableApi::class)
    private fun openEqualizer() {
        try {
            val sessionId = player?.audioSessionId ?: 0
            if (sessionId != 0) {
                val intent = android.content.Intent(this, EqualizerActivity::class.java).apply {
                    putExtra(EqualizerActivity.EXTRA_AUDIO_SESSION_ID, sessionId)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Audio session not available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening equalizer: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_READY -> {
                totalDuration = (player?.duration ?: 0).toInt() / 1000
                songSeekBar.max = totalDuration
                totalTimeText.text = formatTime(totalDuration)
            }

            Player.STATE_ENDED -> {
                skipToNext()
            }

            Player.STATE_BUFFERING -> {
                // Handle buffering state if needed
            }

            Player.STATE_IDLE -> {
                // Handle idle state if needed
            }
        }
    }

    override fun onStart() {
       super.onStart()

        if (isPlaying) {
            player?.play()
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

    override fun onStop() {
        super.onStop()
        player?.pause()
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBarRunnable)
        player?.release()
        player = null
    }

    private fun loadPlaylistSongs() {
        viewModel.getPlaylist(playlistId!!) { playlist ->
            if (playlist != null) {
                viewModel.loadPlaylistSongs(playlist)

                // Observe the playlist songs
                viewModel.playlistSongs.observe(this) { songs ->
                    if (songs.isNotEmpty()) {
                        playlistSongs = songs

                        // Find the current song index in the playlist
                        val songId = intent.getStringExtra(EXTRA_SONG_ID) ?: ""
                        currentSongIndex = playlistSongs.indexOfFirst { it.id == songId }

                        // Update the track position text
                        updateTrackPositionText()
                    }
                }
            }
        }
    }

    private fun updateTrackPositionText() {
        if (playlistSongs.isNotEmpty() && currentSongIndex != -1) {
            val trackPositionText = findViewById<TextView>(R.id.trackPositionText)
            trackPositionText.text = "${currentSongIndex + 1}/${playlistSongs.size}"
        }
    }

    companion object {
        const val EXTRA_SONG_TITLE = "song_title"
        const val EXTRA_ARTIST_NAME = "artist_name"
        const val EXTRA_ALBUM_NAME = "album_name"
        const val EXTRA_TRACK_NUMBER = "track_number"
        const val EXTRA_TOTAL_TRACKS = "total_tracks"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_IMAGE_URL = "image_url"
        const val EXTRA_AUDIO_URL = "audio_url"
        const val EXTRA_SONG_ID = "song_id"
        const val EXTRA_PLAYLIST_ID = "playlist_id"
        const val EXTRA_SONG_ID_LIST = "song_id_list" // New constant
        const val EXTRA_SOURCE_TYPE = "source_type"   // New constant
    }
}