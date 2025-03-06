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
    private val songs: List<Song>,
    private val listener: OnSongClickListener
) : RecyclerView.Adapter<PlaylistSongsAdapter.SongViewHolder>() {

    interface OnSongClickListener {
        fun onSongClick(song: Song, position: Int)
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
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist
        holder.songDuration.text = formatDuration(song.durationInSeconds)

        // Show favorite icon if the song is favorite
        if (song.isFavorite) {
            holder.favoriteIcon.visibility = View.VISIBLE
            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            holder.favoriteIcon.visibility = View.GONE
        }

        // If this is the currently playing song, show the indicator
        // This is hardcoded for the third song in this example
        if (position == 2) {
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
}