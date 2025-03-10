package com.example.appbardemo.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MusicViewModel : ViewModel() {
    private val _songs = MutableLiveData<List<SongModel>>()
    val songs: LiveData<List<SongModel>> = _songs

    private val _playlists = MutableLiveData<List<PlaylistModel>>()
    val playlists: LiveData<List<PlaylistModel>> = _playlists

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentPlaylist = MutableLiveData<PlaylistModel?>()
    val currentPlaylist: LiveData<PlaylistModel?> = _currentPlaylist

    private val _playlistSongs = MutableLiveData<List<SongModel>>()
    val playlistSongs: LiveData<List<SongModel>> = _playlistSongs

    init {
        loadSongs()
        loadPlaylists()
    }

    fun loadSongs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val firebaseSongs = tryLoadFromFirebase()
                _songs.value = firebaseSongs

            } catch (e: Exception) {

                e.printStackTrace()
                _error.value = "Failed to load songs: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("playlists").get().await()
                val loadedPlaylists = snapshot.toObjects(PlaylistModel::class.java)

                _playlists.value = loadedPlaylists

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to load playlists: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun tryLoadFromFirebase(): List<SongModel> = withContext(Dispatchers.IO) {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("songs").get().await()
            return@withContext snapshot.toObjects(SongModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    fun updateFavoriteStatus(songId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {

                val db = FirebaseFirestore.getInstance()
                db.collection("songs").document(songId)
                    .update("isFavorite", isFavorite)
                    .addOnFailureListener {
                        // Ignore Firebase errors - just update local data
                    }

                val currentSongs = _songs.value?.toMutableList() ?: mutableListOf()
                val index = currentSongs.indexOfFirst { it.id == songId }

                if (index != -1) {
                    currentSongs[index] = currentSongs[index].copy(isFavorite = isFavorite)
                    _songs.value = currentSongs
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Don't show error to user for favorite updates
            }
        }
    }

    fun selectPlaylist(playlistId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val playlist = _playlists.value?.find { it.id == playlistId }
                _currentPlaylist.value = playlist

                if (playlist != null) {
                    loadPlaylistSongs(playlist)
                } else {
                    _playlistSongs.value = emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to load playlist: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addSongToPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val playlistRef = db.collection("playlists").document(playlistId)

                val playlistDoc = playlistRef.get().await()
                val playlist = playlistDoc.toObject(PlaylistModel::class.java)

                if (playlist != null && !playlist.songIds.contains(songId)) {

                    val updatedSongIds = playlist.songIds + songId

                    // Update the playlist document
                    playlistRef.update("songIds", updatedSongIds)

                    // Update local data
                    val currentPlaylists = _playlists.value?.toMutableList() ?: mutableListOf()
                    val index = currentPlaylists.indexOfFirst { it.id == playlistId }

                    if (index != -1) {
                        currentPlaylists[index] = currentPlaylists[index].copy(songIds = updatedSongIds)
                        _playlists.value = currentPlaylists
                    }

                    // If this is the currently selected playlist, refresh its songs
                    if (_currentPlaylist.value?.id == playlistId) {
                        selectPlaylist(playlistId)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to add song to playlist: ${e.message}"
            }
        }
    }

    fun removeSongFromPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val playlistRef = db.collection("playlists").document(playlistId)

                // First get the current song IDs
                val playlistDoc = playlistRef.get().await()
                val playlist = playlistDoc.toObject(PlaylistModel::class.java)

                if (playlist != null && playlist.songIds.contains(songId)) {
                    // Remove the song ID from the list
                    val updatedSongIds = playlist.songIds.filter { it != songId }

                    // Update the playlist document
                    playlistRef.update("songIds", updatedSongIds)

                    // Update local data
                    val currentPlaylists = _playlists.value?.toMutableList() ?: mutableListOf()
                    val index = currentPlaylists.indexOfFirst { it.id == playlistId }

                    if (index != -1) {
                        currentPlaylists[index] = currentPlaylists[index].copy(songIds = updatedSongIds)
                        _playlists.value = currentPlaylists
                    }

                    // If this is the currently selected playlist, refresh its songs
                    if (_currentPlaylist.value?.id == playlistId) {
                        selectPlaylist(playlistId)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to remove song from playlist: ${e.message}"
            }
        }
    }

    fun createPlaylist(title: String, description: String, coverImageUrl: String = "") {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()

                // Create a new playlist document
                val newPlaylist = PlaylistModel(
                    title = title,
                    description = description,
                    coverImageUrl = coverImageUrl,
                    songIds = emptyList(),
                    totalDuration = 0,
                    createdBy = "current_user",
                    createdAt = System.currentTimeMillis(),
                    isPublic = true
                )

                val docRef = db.collection("playlists").add(newPlaylist).await()

                // Add the document ID to the playlist
                val playlistWithId = newPlaylist.copy(id = docRef.id)

                // Update the local playlists list
                val currentPlaylists = _playlists.value?.toMutableList() ?: mutableListOf()
                currentPlaylists.add(playlistWithId)
                _playlists.value = currentPlaylists

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to create playlist: ${e.message}"
            }
        }
    }


    fun loadPlaylistSongs(playlist: PlaylistModel) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                if (playlist.songIds.isEmpty()) {
                    _playlistSongs.value = emptyList()
                    return@launch
                }

                val result = fetchSongsByIds(playlist.songIds)
                _playlistSongs.value = result

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to load playlist songs: ${e.message}"
                _playlistSongs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    private suspend fun fetchSongsByIds(songIds: List<String>): List<SongModel> = withContext(Dispatchers.IO) {
        if (songIds.isEmpty()) return@withContext emptyList<SongModel>()

        try {
            // First check if we already have these songs loaded
            val cachedSongs = _songs.value ?: emptyList()
            val cachedSongMap = cachedSongs.associateBy { it.id }

            // Find which song IDs we need to fetch
            val missingIds = songIds.filter { !cachedSongMap.containsKey(it) }

            if (missingIds.isEmpty()) {
                // All songs are already loaded, just return them in the correct order
                return@withContext songIds.mapNotNull { cachedSongMap[it] }
            }

            // We need to fetch some songs from the database
            val db = FirebaseFirestore.getInstance()

            // Split into batches if there are many IDs (Firestore has limits)
            val batchSize = 10
            val batches = missingIds.chunked(batchSize)

            val fetchedSongs = mutableListOf<SongModel>()

            for (batch in batches) {
                // Create a list to collect the document snapshots
                val documents = mutableListOf<DocumentSnapshot>()

                // Fetch each document one by one (no async here)
                for (songId in batch) {
                    try {
                        val doc = db.collection("songs").document(songId).get().await()
                        if (doc.exists()) {
                            documents.add(doc)
                        }
                    } catch (e: Exception) {
                        // Skip failed documents but continue with others
                        e.printStackTrace()
                    }
                }

                // Convert documents to SongModel objects
                val batchSongs = documents.mapNotNull {
                    it.toObject(SongModel::class.java)
                }

                fetchedSongs.addAll(batchSongs)
            }

            // Combine cached and fetched songs
            val allSongsMap = (cachedSongs + fetchedSongs).associateBy { it.id }

            // Return songs in the order of the original songIds
            return@withContext songIds.mapNotNull { allSongsMap[it] }
        } catch (e: Exception) {
            e.printStackTrace()
            // Return any songs we have cached if the fetch fails
            val cachedSongs = _songs.value ?: emptyList()
            val cachedSongMap = cachedSongs.associateBy { it.id }
            return@withContext songIds.mapNotNull { cachedSongMap[it] }
        }
    }
    /**
     * Get a playlist by its ID (first from cache, then from Firebase if needed)
     */
    fun getPlaylist(playlistId: String, callback: (PlaylistModel?) -> Unit) {
        viewModelScope.launch {
            try {
                // Check if we have this playlist in cache
                val cachedPlaylists = _playlists.value ?: emptyList()
                val cachedPlaylist = cachedPlaylists.find { it.id == playlistId }

                if (cachedPlaylist != null) {
                    callback(cachedPlaylist)
                    return@launch
                }

                // If not in cache, fetch from Firebase
                val db = FirebaseFirestore.getInstance()
                val document = db.collection("playlists").document(playlistId).get().await()

                if (document.exists()) {
                    val playlist = document.toObject(PlaylistModel::class.java)
                    callback(playlist)
                } else {
                    callback(null)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
}