package com.example.appbardemo.ui.theme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appbardemo.R

class PlaylistsAdapter(
    private var playlists: List<PlaylistModel>
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.gridImageView)
        val title: TextView = view.findViewById(R.id.gridTitleText)
        val subtitle: TextView = view.findViewById(R.id.gridSubtitleText)
        val optionsButton: ImageView = view.findViewById(R.id.gridOptionsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]

        holder.title.text = playlist.title
        holder.subtitle.text = "${playlist.songIds.size} songs • ${playlist.getFormattedDuration()}"

        // Load image using Glide
        if (playlist.coverImageUrl.isNotEmpty()) {
            Glide.with(holder.image.context)
                .load(playlist.coverImageUrl)
                .apply(RequestOptions().placeholder(R.drawable.album))
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.album)
        }

        // Set click listener to open playlist
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlaylistActivity::class.java).apply {
                putExtra(PlaylistActivity.EXTRA_PLAYLIST_ID, playlist.id)
                putExtra(PlaylistActivity.EXTRA_PLAYLIST_TITLE, playlist.title)
                putExtra(PlaylistActivity.EXTRA_SONGS_COUNT, playlist.songIds.size)
                putExtra(PlaylistActivity.EXTRA_PLAYLIST_DURATION, playlist.getFormattedDuration())
            }
            holder.itemView.context.startActivity(intent)
        }

        // Set options menu click listener
        holder.optionsButton.setOnClickListener {
            // Show options menu (e.g., edit, delete, share)
        }
    }

    override fun getItemCount(): Int = playlists.size

    fun updateData(newPlaylists: List<PlaylistModel>) {
        this.playlists = newPlaylists
        notifyDataSetChanged()
    }
}