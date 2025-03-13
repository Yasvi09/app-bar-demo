package com.example.appbardemo.ui.theme

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appbardemo.R


class PlaylistDialogHelper(private val context: Context) {

    fun showCreatePlaylistDialog(
        viewModel: MusicViewModel,
        onSuccess: (() -> Unit)? = null
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_playlist, null)
        dialog.setContentView(view)

        val layoutParams = dialog.window?.attributes
        layoutParams?.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams

        val titleInput = view.findViewById<EditText>(R.id.playlistTitleInput)
        val descriptionInput = view.findViewById<EditText>(R.id.playlistDescriptionInput)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val createButton = view.findViewById<Button>(R.id.createButton)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        createButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (title.isEmpty()) {
                titleInput.error = "Please enter a title"
                return@setOnClickListener
            }

            // Create the playlist
            viewModel.createPlaylist(title, description)

            Toast.makeText(context, "Playlist created", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            // Call success callback
            onSuccess?.invoke()
        }

        dialog.show()
    }


    fun showAddToPlaylistDialog(
        viewModel: MusicViewModel,
        songId: String
    ) {
        val playlists = viewModel.playlists.value ?: emptyList()

        if (playlists.isEmpty()) {
            // Show create playlist dialog instead
            Toast.makeText(context, "No playlists. Create one first.", Toast.LENGTH_SHORT).show()
            showCreatePlaylistDialog(viewModel) {
                // After creating a playlist, try to show this dialog again
                showAddToPlaylistDialog(viewModel, songId)
            }
            return
        }

        // Create a list of playlist names
        val playlistNames = playlists.map { it.title }.toTypedArray()

        // Show dialog with playlist options
        android.app.AlertDialog.Builder(context)
            .setTitle("Add to Playlist")
            .setItems(playlistNames) { _, which ->
                val selectedPlaylist = playlists[which]
                viewModel.addSongToPlaylist(selectedPlaylist.id, songId)
                Toast.makeText(context, "Added to ${selectedPlaylist.title}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Create New Playlist") { _, _ ->
                showCreatePlaylistDialog(viewModel)
            }
            .create()
            .show()
    }
}