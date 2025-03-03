package com.example.appbardemo.ui.theme

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

    // View holder for song items
    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.musicIcon)
        val title: TextView = itemView.findViewById(R.id.titleText)
        val subtitle: TextView = itemView.findViewById(R.id.subtitleText)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
        val optionsButton: ImageView = itemView.findViewById(R.id.optionsButton)
    }

    // View holder for grid items (album, artist, playlist)
    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.gridImageView)
        val title: TextView = itemView.findViewById(R.id.gridTitleText)
        val subtitle: TextView = itemView.findViewById(R.id.gridSubtitleText)
        val optionsButton: ImageView = itemView.findViewById(R.id.gridOptionsButton)
    }

    override fun getItemViewType(position: Int): Int {
        // Return view type based on tab type
        return when (type) {
            "album", "artist", "playlist" -> VIEW_TYPE_GRID
            else -> VIEW_TYPE_SONG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_grid, parent, false)
                GridViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_music, parent, false)
                SongViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is GridViewHolder -> {
                // Bind data to grid view (album, artist, playlist)
                holder.title.text = item.title
                holder.subtitle.text = item.subtitle
                holder.image.setImageResource(item.iconResId)

                holder.optionsButton.setOnClickListener {
                    // Show options menu
                }

                holder.itemView.setOnClickListener {
                    // Handle item click
                }
            }

            is SongViewHolder -> {
                // Bind data to song view
                holder.title.text = item.title
                holder.subtitle.text = item.subtitle
                holder.icon.setImageResource(item.iconResId)

                // Show favorite button only for songs
                if (type == "songs") {
                    holder.favoriteButton.visibility = View.VISIBLE
                } else {
                    holder.favoriteButton.visibility = View.GONE
                }

                holder.itemView.setOnClickListener {
                    // Handle item click
                }

                holder.optionsButton.setOnClickListener {
                    // Show options menu
                }

                holder.favoriteButton.setOnClickListener {
                    // Toggle favorite status
                    toggleFavorite(holder.favoriteButton)
                }
            }
        }
    }

    private fun toggleFavorite(favoriteButton: ImageView) {
        // Toggle between filled and outlined favorite icon
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