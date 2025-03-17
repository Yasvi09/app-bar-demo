package com.example.appbardemo.ui.theme

import ProgressDialog
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.appbardemo.R
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaCodec
import android.media.MediaMuxer
import android.util.Log
import java.io.IOException
import java.nio.ByteBuffer

class Mp3EditorActivity : AppCompatActivity() {
    private lateinit var waveformView: ImageView
    private lateinit var playButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var songTitleTextView: TextView
    private lateinit var timeMarkersTextView: TextView
    private lateinit var cutTimeInfoTextView: TextView
    private lateinit var selectionOverlayView: WaveformSelectionView

    private var mediaPlayer: MediaPlayer? = null
    private var songUrl: String = ""
    private var songId: String = ""
    private var songName: String = ""
    private var songArtist: String = ""
    private var songDuration: Int = 0

    private var startTime: Int = 0
    private var endTime: Int = 0
    private var isPlaying = false
    private var zoomLevel = 1.0f
    private var waveformBitmap: Bitmap? = null

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var animateWaveformRunnable: Runnable
    private var waveformAnimator: ValueAnimator? = null

    private var currentPlaybackPosition = 0
    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3_editor)

        waveformView = findViewById(R.id.waveformView)
        playButton = findViewById(R.id.playButton)
        backButton = findViewById(R.id.backButton)
        songTitleTextView = findViewById(R.id.songTitleTextView)
        timeMarkersTextView = findViewById(R.id.timeMarkers)
        cutTimeInfoTextView = findViewById(R.id.cutTimeInfo)

        selectionOverlayView = WaveformSelectionView(this)
        val waveformContainer = findViewById<FrameLayout>(R.id.waveformContainer)
        waveformContainer.addView(selectionOverlayView)

        // Initialize gesture detector for handling pinch zoom
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                // Handle drag to adjust selection
                selectionOverlayView.handleDrag(-distanceX)
                updateTimeMarkers()
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Double tap to reset zoom
                zoomLevel = 1.0f
                generateWaveform()
                selectionOverlayView.resetSelection(songDuration)
                updateTimeMarkers()
                return true
            }
        })

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

        // Setup our selection view with song duration
        selectionOverlayView.setSongDuration(songDuration)
        selectionOverlayView.setSelectionChangeListener { start, end ->
            startTime = start
            endTime = end
            updateCutTimeInfo()
        }

        // Setup click listeners
        setupClickListeners()

        // Initialize media player
        initializeMediaPlayer()

        // Generate waveform
        generateWaveform()

        // Initialize the seek bar updater
        setupSeekBarUpdater()

        // Setup waveform animation
        setupWaveformAnimation()

        // Initial update of time displays
        updateTimeMarkers()
        updateCutTimeInfo()
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            togglePlayback()
        }

        findViewById<ImageView>(R.id.zoomInButton).setOnClickListener {
            zoomLevel = min(zoomLevel * 1.5f, 4.0f)
            generateWaveform()
            updateTimeMarkers()
        }

        findViewById<ImageView>(R.id.zoomOutButton).setOnClickListener {
            zoomLevel = max(zoomLevel / 1.5f, 0.5f)
            generateWaveform()
            updateTimeMarkers()
        }

        findViewById<ImageView>(R.id.checkButton).setOnClickListener {
            saveAudioCut()
        }

        // Set touch listeners for the waveform container
        findViewById<View>(R.id.waveformContainer).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
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
                currentPlaybackPosition = 0
                selectionOverlayView.updatePlaybackPosition(0)
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
            waveformAnimator?.pause()
        } else {
            // Set the start position and play
            mediaPlayer?.seekTo(startTime * 1000)
            mediaPlayer?.start()
            playButton.setImageResource(R.drawable.ic_pause)
            handler.postDelayed(updateSeekBarRunnable, 100)
            currentPlaybackPosition = startTime
            waveformAnimator?.start()
        }

        isPlaying = !isPlaying
    }

    private fun setupSeekBarUpdater() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer != null && isPlaying) {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    currentPlaybackPosition = currentPosition / 1000

                    // Update the playback position indicator
                    selectionOverlayView.updatePlaybackPosition(currentPlaybackPosition)

                    // If reached the end of selection, stop playback
                    if (currentPosition >= endTime * 1000) {
                        mediaPlayer?.pause()
                        mediaPlayer?.seekTo(startTime * 1000)
                        isPlaying = false
                        playButton.setImageResource(R.drawable.ic_play)
                        waveformAnimator?.cancel()
                        return
                    }

                    handler.postDelayed(this, 50)
                }
            }
        }
    }

    private fun setupWaveformAnimation() {
        waveformAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000 // 1 second animation duration
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                // This will trigger a redraw of the waveform with animation
                selectionOverlayView.invalidate()
            }
        }
    }

    private fun generateWaveform() {
        // Create a bitmap for the waveform
        val width = resources.displayMetrics.widthPixels
        val height = 250 // Match the height in the layout

        if (waveformBitmap == null || waveformBitmap?.width != width || waveformBitmap?.height != height) {
            waveformBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(waveformBitmap!!)
        val paint = Paint().apply {
            color = Color.rgb(203, 251, 11) // Green accent color
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // Clear canvas
        canvas.drawColor(Color.BLACK)

        // In a real app, you would analyze the audio file to generate a waveform
        // For this example, we'll generate a random waveform based on song duration and zoom
        val random = Random(songId.hashCode()) // Use song ID as seed for consistent randomness
        val baselineY = height / 2f

        // Calculate visible duration based on zoom
        val visibleDuration = (songDuration / zoomLevel).toInt()
        val pointsPerSecond = 10
        val totalPoints = visibleDuration * pointsPerSecond

        val path = android.graphics.Path()
        var lastX = 0f
        var lastY = baselineY

        path.moveTo(lastX, lastY)

        for (i in 0 until totalPoints) {
            val x = i * (width.toFloat() / totalPoints)
            val timePosition = i.toFloat() / pointsPerSecond

            // Generate amplitude based on time position
            val amplitude = (random.nextFloat() * 40) + 10

            // Add some patterns to make it look more like a real waveform
            val frequency = 1 + (abs(((timePosition * 5) % 10) - 5) / 2.5f)
            val calculatedAmplitude = amplitude * frequency

            val y = baselineY + if (i % 2 == 0) calculatedAmplitude else -calculatedAmplitude

            path.lineTo(x, y)
            lastX = x
            lastY = y
        }

        // Draw the path
        canvas.drawPath(path, paint)

        // Set the bitmap to the ImageView
        waveformView.setImageBitmap(waveformBitmap)

        // Update the selection view with new zoom level
        selectionOverlayView.setZoomLevel(zoomLevel)
    }

    private fun updateTimeMarkers() {
        // Create time markers based on the zoom level
        val visibleDuration = (songDuration / zoomLevel).toInt()
        val numMarkers = 6 // Number of time markers to display
        val markerInterval = visibleDuration / (numMarkers - 1)

        val markerBuilder = StringBuilder()
        for (i in 0 until numMarkers) {
            val time = i * markerInterval
            markerBuilder.append(formatTime(time))
            if (i < numMarkers - 1) {
                markerBuilder.append("  ")
            }
        }

        timeMarkersTextView.text = markerBuilder.toString()
    }

    private fun updateCutTimeInfo() {
        cutTimeInfoTextView.text = "Selected: ${formatTime(startTime)} - ${formatTime(endTime)}"
    }

    @SuppressLint("WrongConstant")
    private fun saveAudioCut() {
        Toast.makeText(this, "Saving cut audio from ${formatTime(startTime)} to ${formatTime(endTime)}", Toast.LENGTH_SHORT).show()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Processing audio...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Run audio processing in a background thread
        Thread {
            try {
                // Get the Download folder path
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val fileName = "${songName.replace(' ', '_')}${formatTime(startTime).replace(':', '_')}${formatTime(endTime).replace(':', '_')}.m4a"
                val outputFile = File(downloadDir, fileName)

                // Create the output file
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs()
                }

                // Use MediaExtractor and MediaMuxer to trim the audio
                val extractor = MediaExtractor()
                extractor.setDataSource(songUrl)

                // Find the audio track
                val numTracks = extractor.trackCount
                var audioTrackIndex = -1
                var format: MediaFormat? = null

                for (i in 0 until numTracks) {
                    val trackFormat = extractor.getTrackFormat(i)
                    val mime = trackFormat.getString(MediaFormat.KEY_MIME)
                    Log.d("AudioDebug", "Track $i - MIME Type: $mime")
                    if (mime?.startsWith("audio/") == true) {
                        audioTrackIndex = i
                        format = trackFormat
                        break
                    }
                }

                if (audioTrackIndex < 0 || format == null) {
                    throw IOException("No audio track found in $songUrl")
                    Log.d("AudioFormat", "Selected format: $format")
                }

                Log.d("AudioFormat", "Selected format: $format")

                // Set up MediaMuxer for the output file
                val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
                val dstAudioTrackIndex = format?.let { muxer.addTrack(it) }
                muxer.start()

                // Select the audio track
                extractor.selectTrack(audioTrackIndex)

                // Convert time to microseconds
                val startTimeUs = startTime * 1000000L
                val endTimeUs = endTime * 1000000L

                // Seek to start position
                extractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

                // Allocate buffer for reading
                val maxBufferSize = 1024 * 1024
                val buffer = ByteBuffer.allocate(maxBufferSize)
                val bufferInfo = MediaCodec.BufferInfo()

                // Check if we haven't reached the end position
                while (true) {
                    // Read sample data
                    val sampleSize = extractor.readSampleData(buffer, 0)

                    // Break if no more samples or we've reached the end time
                    if (sampleSize < 0 || extractor.sampleTime > endTimeUs) {
                        break
                    }

                    // Set buffer info
                    bufferInfo.size = sampleSize
                    bufferInfo.offset = 0
                    bufferInfo.flags = extractor.sampleFlags
                    bufferInfo.presentationTimeUs = extractor.sampleTime - startTimeUs

                    // Write the sample to the muxer
                    dstAudioTrackIndex?.let { muxer.writeSampleData(it, buffer, bufferInfo) }

                    // Advance to the next sample
                    extractor.advance()
                }

                // Release resources
                muxer.stop()
                muxer.release()
                extractor.release()

                // Add the file to the media store
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(outputFile.absolutePath),
                    arrayOf("audio/mp3"),
                    null
                )

                // Update UI on main thread
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Audio saved to Downloads: $fileName",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()

                // Handle failure on main thread
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Failed to save audio: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
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
        waveformAnimator?.cancel()
    }


    inner class WaveformSelectionView(context: Context) : View(context) {
        private val paint = Paint()
        private val selectionPaint = Paint()
        private val playbackPaint = Paint()
        private val textPaint = Paint()

        private var startSelectionX = 0f
        private var endSelectionX = 0f
        private var startHandleRadius = 20f
        private var songDurationMs = 0
        private var currentZoom = 1f
        private var playbackPosition = 0
        private var activeHandle = 0 // 0=none, 1=left, 2=right, 3=middle
        private var lastTouchX = 0f
        private var selectionChangeListener: ((Int, Int) -> Unit)? = null
        private var isInitialized = false

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)

            // Initialize selection positions when the view size is first determined
            if (!isInitialized && w > 0) {
                startSelectionX = w * 0.25f
                endSelectionX = w * 0.75f
                isInitialized = true
                notifySelectionChanged()
                invalidate()
            }
        }

        init {
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f

            selectionPaint.color = Color.WHITE
            selectionPaint.style = Paint.Style.FILL
            selectionPaint.alpha = 50

            playbackPaint.color = Color.RED
            playbackPaint.style = Paint.Style.STROKE
            playbackPaint.strokeWidth = 3f

            textPaint.color = Color.WHITE
            textPaint.textSize = 24f
        }

        fun setSongDuration(durationInSeconds: Int) {
            songDurationMs = durationInSeconds
            resetSelection(durationInSeconds)
        }

        fun setZoomLevel(zoom: Float) {
            currentZoom = zoom
            invalidate()
        }

        fun resetSelection(durationInSeconds: Int) {
            // Ensure width is valid before calculating
            if (width > 0) {
                startSelectionX = width * 0.25f
                endSelectionX = width * 0.75f
            } else {
                // Fallback values if width is not yet determined
                startSelectionX = 0f
                endSelectionX = 100f  // Any positive value to ensure a valid range

                // Post a delayed reset once the view is properly measured
                post {
                    if (width > 0) {
                        startSelectionX = width * 0.25f
                        endSelectionX = width * 0.75f
                        notifySelectionChanged()
                        invalidate()
                    }
                }
            }
            notifySelectionChanged()
            invalidate()
        }

        fun setSelectionChangeListener(listener: (Int, Int) -> Unit) {
            selectionChangeListener = listener
        }

        fun updatePlaybackPosition(positionInSeconds: Int) {
            playbackPosition = positionInSeconds
            invalidate()
        }

        fun handleDrag(dx: Float) {
            when (activeHandle) {
                1 -> { // Left handle
                    // Check if the range would be valid before applying coerceIn
                    val newStartX = startSelectionX + dx
                    val minBound = 0f
                    val maxBound = endSelectionX - startHandleRadius * 4

                    // Only apply if the range is valid
                    if (maxBound > minBound) {
                        startSelectionX = newStartX.coerceIn(minBound, maxBound)
                    } else {
                        // Fallback for invalid range
                        startSelectionX = minBound
                    }
                }
                2 -> { // Right handle
                    // Check if the range would be valid before applying coerceIn
                    val newEndX = endSelectionX + dx
                    val minBound = startSelectionX + startHandleRadius * 4
                    val maxBound = width.toFloat()

                    // Only apply if the range is valid
                    if (maxBound > minBound) {
                        endSelectionX = newEndX.coerceIn(minBound, maxBound)
                    } else {
                        // Fallback for invalid range
                        endSelectionX = maxBound
                    }
                }
                3 -> { // Middle (move both)
                    val selectionWidth = endSelectionX - startSelectionX

                    // Add safeguards
                    if (selectionWidth > 0 && width > 0) {
                        val newStartX = startSelectionX + dx
                        val maxStart = width - selectionWidth

                        if (maxStart >= 0) {
                            startSelectionX = newStartX.coerceIn(0f, maxStart)
                            endSelectionX = startSelectionX + selectionWidth
                        }
                    }
                }
            }

            if (activeHandle != 0) {
                notifySelectionChanged()
                invalidate()
            }
        }

        private fun notifySelectionChanged() {
            // Convert selection positions to time values
            val visibleDuration = (songDurationMs / currentZoom)
            val startTime = ((startSelectionX / width) * visibleDuration).toInt()
            val endTime = ((endSelectionX / width) * visibleDuration).toInt()

            selectionChangeListener?.invoke(startTime, endTime)
        }

        @Override
        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Check if touch is on handles or in selection
                    if (abs(x - startSelectionX) < startHandleRadius * 2) {
                        activeHandle = 1 // Left handle
                    } else if (abs(x - endSelectionX) < startHandleRadius * 2) {
                        activeHandle = 2 // Right handle
                    } else if (x > startSelectionX && x < endSelectionX) {
                        activeHandle = 3 // Middle (move both)
                    } else {
                        activeHandle = 0
                        return false // Let parent handle the event
                    }
                    lastTouchX = x
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (activeHandle != 0) {
                        val dx = x - lastTouchX
                        handleDrag(dx)
                        lastTouchX = x
                        return true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    activeHandle = 0
                }
            }

            return super.onTouchEvent(event)
        }

        @Override
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Draw semi-transparent overlay outside selection
            canvas.drawRect(0f, 0f, startSelectionX, height.toFloat(), selectionPaint)
            canvas.drawRect(endSelectionX, 0f, width.toFloat(), height.toFloat(), selectionPaint)

            // Draw selection border
            canvas.drawRect(startSelectionX, 0f, endSelectionX, height.toFloat(), paint)

            // Draw handles
            canvas.drawCircle(startSelectionX, height / 2f, startHandleRadius, paint)
            canvas.drawCircle(endSelectionX, height / 2f, startHandleRadius, paint)

            // Draw current playback position line
            if (isPlaying && playbackPosition >= startTime && playbackPosition <= endTime) {
                val visibleDuration = (songDurationMs / currentZoom)
                val playbackX = (playbackPosition.toFloat() / visibleDuration) * width
                canvas.drawLine(playbackX, 0f, playbackX, height.toFloat(), playbackPaint)
            }
        }
    }
}