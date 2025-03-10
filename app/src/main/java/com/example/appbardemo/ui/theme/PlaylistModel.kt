package com.example.appbardemo.ui.theme

import com.google.firebase.firestore.DocumentId

data class PlaylistModel(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val coverImageUrl: String = "",
    val songIds: List<String> = emptyList(),
    val totalDuration: Int = 0,
    val createdBy: String = "",
    val createdAt: Long = 0,
    val isPublic: Boolean = true
) {
    fun getFormattedDuration(): String {
        val hours = totalDuration / 3600
        val minutes = (totalDuration % 3600) / 60

        return when {
            hours > 0 -> "$hours:${minutes.toString().padStart(2, '0')} Hours"
            else -> "$minutes Minutes"
        }
    }

    // Helper method to check if the playlist contains a specific song
    fun containsSong(songId: String): Boolean {
        return songIds.contains(songId)
    }
}