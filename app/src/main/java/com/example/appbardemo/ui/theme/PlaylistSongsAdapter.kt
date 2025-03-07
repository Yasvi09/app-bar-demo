package com.example.appbardemo.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R
import java.util.concurrent.TimeUnit

class PlaylistSongsAdapter(
    private var songs: List<SongModel>,
    private val listener: OnSongClickListener
) : RecyclerView.Adapter<PlaylistSongsAdapter.SongViewHolder>() {

    // Track the currently playing song position (if any)
    private var currentlyPlayingPosition: Int = -1

    interface OnSongClickListener {
        fun onSongClick(song: SongModel, position: Int)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNumber: TextView = itemView.findViewById(R.id.songNumberText)
        val songTitle: TextView = itemView.findViewById(R.id.songTitleText)
        val songArtist: TextView = itemView.findViewById(R.id.songArtistText)
        val songDuration: TextView = itemView.findViewById(R.id.songDurationText)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
        val nowPlayingIndicator: ImageView = itemView.findViewById(R.id.nowPlayingIndicator)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Update currently playing position
                    setCurrentlyPlaying(position)

                    // Trigger the click listener
                    listener.onSongClick(songs[position], position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        holder.songNumber.text = (position + 1).toString()
        holder.songTitle.text = song.name
        holder.songArtist.text = song.artist
        holder.songDuration.text = formatDuration(song.duration)

        // Show favorite icon if the song is favorite
        if (song.isFavorite) {
            holder.favoriteIcon.visibility = View.VISIBLE
            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            holder.favoriteIcon.visibility = View.GONE
        }

        // Show playing indicator if this is the currently playing song
        if (position == currentlyPlayingPosition) {
            holder.nowPlayingIndicator.visibility = View.VISIBLE
            holder.songNumber.visibility = View.INVISIBLE
        } else {
            holder.nowPlayingIndicator.visibility = View.GONE
            holder.songNumber.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = songs.size

    private fun formatDuration(seconds: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    fun updateSongs(newSongs: List<SongModel>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }

    fun getSongs(): List<SongModel> {
        return songs
    }

    fun setCurrentlyPlaying(position: Int) {
        val oldPosition = currentlyPlayingPosition
        currentlyPlayingPosition = position

        // Update the indicators for the previous and new positions
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition)
        }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    fun setCurrentlyPlayingSong(songId: String) {
        val position = songs.indexOfFirst { it.id == songId }
        if (position != -1) {
            setCurrentlyPlaying(position)
        }
    }
}