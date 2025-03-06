package com.example.appbardemo.ui.theme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class MusicAdapter(
    private val items: List<MusicItem>,
    private val type: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SONG = 0
        private const val VIEW_TYPE_GRID = 1
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.musicIcon)
        val title: TextView = itemView.findViewById(R.id.titleText)
        val subtitle: TextView = itemView.findViewById(R.id.subtitleText)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
        val optionsButton: ImageView = itemView.findViewById(R.id.optionsButton)
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.gridImageView)
        val title: TextView = itemView.findViewById(R.id.gridTitleText)
        val subtitle: TextView = itemView.findViewById(R.id.gridSubtitleText)
        val optionsButton: ImageView = itemView.findViewById(R.id.gridOptionsButton)
    }

    override fun getItemViewType(position: Int): Int {
        return when (type) {
            "album", "artist", "playlist" -> VIEW_TYPE_GRID
            else -> VIEW_TYPE_SONG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
                GridViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
                SongViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is GridViewHolder -> {
                holder.title.text = item.title
                holder.subtitle.text = item.subtitle
                holder.image.setImageResource(item.iconResId)

                holder.optionsButton.setOnClickListener {
                    // Handle options menu click
                }

                holder.itemView.setOnClickListener {
                    when (type) {
                        "album" -> {
                            // Open album details
                        }
                        "artist" -> {
                            // Open artist details
                        }
                        "playlist" -> {
                            val intent = Intent(holder.itemView.context, PlaylistActivity::class.java).apply {
                                putExtra(PlaylistActivity.EXTRA_PLAYLIST_TITLE, item.title)
                                putExtra(PlaylistActivity.EXTRA_SONGS_COUNT, 8) // Mock count
                                putExtra(PlaylistActivity.EXTRA_PLAYLIST_DURATION, "1:02 Hours") // Mock duration
                            }
                            holder.itemView.context.startActivity(intent)
                        // Open playlist
                        }
                    }
                }
            }

            is SongViewHolder -> {
                holder.title.text = item.title
                holder.subtitle.text = item.subtitle
                holder.icon.setImageResource(item.iconResId)

                if (type == "songs") {
                    holder.favoriteButton.visibility = View.VISIBLE
                } else {
                    holder.favoriteButton.visibility = View.GONE
                }

                holder.itemView.setOnClickListener {
                    if (type == "songs") {
                        // Launch Play Song Activity
                        val intent = Intent(holder.itemView.context, PlaySongActivity::class.java).apply {
                            putExtra(PlaySongActivity.EXTRA_SONG_TITLE, item.title)
                            putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, item.subtitle)
                            putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, "Your Music")
                            putExtra(PlaySongActivity.EXTRA_TRACK_NUMBER, position + 1)
                            putExtra(PlaySongActivity.EXTRA_TOTAL_TRACKS, items.size)
                            putExtra(PlaySongActivity.EXTRA_DURATION, 180 + position * 30) // Random duration based on position
                            putExtra(PlaySongActivity.EXTRA_IMAGE_RES_ID, item.iconResId)
                            putExtra(PlaySongActivity.EXTRA_SONG_ID, position)
                        }
                        holder.itemView.context.startActivity(intent)
                    }
                }

                holder.optionsButton.setOnClickListener {
                    // Show options menu
                }

                holder.favoriteButton.setOnClickListener {
                    toggleFavorite(holder.favoriteButton)
                }
            }
        }
    }

    private fun toggleFavorite(favoriteButton: ImageView) {
        val currentDrawable = favoriteButton.drawable.constantState
        val outlineDrawable = favoriteButton.context.getDrawable(R.drawable.ic_favorite_border)?.constantState

        if (currentDrawable == outlineDrawable) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    override fun getItemCount(): Int = items.size
}


