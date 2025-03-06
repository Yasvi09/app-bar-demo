package com.example.appbardemo.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MusicViewModel : ViewModel() {
    private val _songs = MutableLiveData<List<SongModel>>()
    val songs: LiveData<List<SongModel>> = _songs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Try to load from Firebase first
                val firebaseSongs = tryLoadFromFirebase()

                println("Firebase songs: $firebaseSongs")

                // If Firebase loading fails or returns empty, use sample data

                    _songs.value = firebaseSongs

            } catch (e: Exception) {
                // Log the error but don't crash
                e.printStackTrace()
                _error.value = "Failed to load songs: ${e.message}"

                // Fall back to sample data

            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun tryLoadFromFirebase(): List<SongModel> = withContext(Dispatchers.IO) {
        try {


            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("songs").get().await()

            println(snapshot)
            return@withContext snapshot.toObjects(SongModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e)
            return@withContext emptyList()
        }
    }

    fun updateFavoriteStatus(songId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                // Try to update in Firebase if available
                val db = FirebaseFirestore.getInstance()
                db.collection("songs").document(songId)
                    .update("isFavorite", isFavorite)
                    .addOnFailureListener {
                        // Ignore Firebase errors - just update local data
                    }

                // Update local data anyway
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
}