package com.example.appbardemo.ui.theme

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class FirebaseSongRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val songsCollection = db.collection("songs")

    suspend fun getAllSongs(): List<SongModel> = withContext(Dispatchers.IO) {
        try {
            val snapshot = songsCollection.get().await()
            return@withContext snapshot.toObjects(SongModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    suspend fun getSongById(songId: String): SongModel? = withContext(Dispatchers.IO) {
        try {
            val document = songsCollection.document(songId).get().await()
            return@withContext document.toObject(SongModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun getAudioUrl(songPath: String): String = withContext(Dispatchers.IO) {
        try {
            // Get a temporary download URL that includes an access token
            val storageRef = storage.reference.child(songPath)
            // URL will be valid for 1 hour
            return@withContext storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ""
        }
    }

    fun updateFavoriteStatus(songId: String, isFavorite: Boolean) {
        songsCollection.document(songId).update("isFavorite", isFavorite)
            .addOnSuccessListener {
                println("Song favorite status updated successfully")
            }
            .addOnFailureListener { e ->
                println("Error updating song favorite status: ${e.message}")
            }
    }
}