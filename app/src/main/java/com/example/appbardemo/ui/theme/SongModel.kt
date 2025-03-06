package com.example.appbardemo.ui.theme

import com.google.firebase.firestore.DocumentId

data class SongModel(
    @DocumentId val id: String = "",
    val name: String = "",
    val artist: String = "",
    val duration: Int = 0,
    val image: String = "",
    val url: String = "",
    val album: String = "",
    var isFavorite: Boolean = false,
    var subtitle: String = ""
) {
    // Helper method to convert to PlaySongActivity parameters
    fun toPlaySongParams(position: Int, totalTracks: Int): Map<String, Any> {
        return mapOf(
            PlaySongActivity.EXTRA_SONG_TITLE to name,
            PlaySongActivity.EXTRA_ARTIST_NAME to artist,
            PlaySongActivity.EXTRA_ALBUM_NAME to album,
            PlaySongActivity.EXTRA_TRACK_NUMBER to position + 1,
            PlaySongActivity.EXTRA_TOTAL_TRACKS to totalTracks,
            PlaySongActivity.EXTRA_DURATION to duration,
            PlaySongActivity.EXTRA_SONG_ID to id,
            PlaySongActivity.EXTRA_AUDIO_URL to url,
            PlaySongActivity.EXTRA_IMAGE_URL to image
        )
    }
}