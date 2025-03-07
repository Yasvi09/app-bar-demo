package com.example.appbardemo.ui.theme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appbardemo.R

class MusicAdapter(
    private var songs: List<SongModel>,
    private val type: String,
    private val onFavoriteClicked: ((SongModel, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var playlistHelper: PlaylistDialogHelper? = null
    private var viewModel: MusicViewModel? = null

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
        val song = songs[position]

        when (holder) {
            is GridViewHolder -> {
                holder.title.text = song.name
                holder.subtitle.text = song.artist

                // Load image using Glide
                if (song.image.isNotEmpty()) {
                    Glide.with(holder.image.context)
                        .load(song.image)
                        .apply(RequestOptions().placeholder(R.drawable.album))
                        .into(holder.image)
                } else {
                    holder.image.setImageResource(R.drawable.album)
                }

                holder.optionsButton.setOnClickListener {
                    showOptionsMenu(it, song)
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
                            // Consider updating PlaylistActivity to receive SongModel list
                        }
                    }
                }
            }

            is SongViewHolder -> {
                holder.title.text = song.name
                holder.subtitle.text = song.artist

                // Load image using Glide
                if (song.image.isNotEmpty()) {
                    Glide.with(holder.icon.context)
                        .load(song.image)
                        .apply(RequestOptions().placeholder(R.drawable.album).centerCrop())
                        .into(holder.icon)
                } else {
                    holder.icon.setImageResource(R.drawable.album)
                }

                if (type == "songs") {
                    holder.favoriteButton.visibility = View.VISIBLE

                    // Set the correct favorite icon
                    if (song.isFavorite) {
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                    } else {
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                    }

                } else {
                    holder.favoriteButton.visibility = View.GONE
                }

                holder.itemView.setOnClickListener {
                    if (type == "songs") {
                        // Launch Play Song Activity with the song data
                        val intent = Intent(holder.itemView.context, PlaySongActivity::class.java).apply {
                            putExtra(PlaySongActivity.EXTRA_SONG_TITLE, song.name)
                            putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, song.artist)
                            putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, song.album)
                            putExtra(PlaySongActivity.EXTRA_TRACK_NUMBER, position + 1)
                            putExtra(PlaySongActivity.EXTRA_TOTAL_TRACKS, songs.size)
                            putExtra(PlaySongActivity.EXTRA_DURATION, song.duration)
                            putExtra(PlaySongActivity.EXTRA_SONG_ID, song.id)
                            putExtra(PlaySongActivity.EXTRA_AUDIO_URL, song.url)
                            putExtra(PlaySongActivity.EXTRA_IMAGE_URL, song.image)
                        }
                        holder.itemView.context.startActivity(intent)
                    }
                }

                holder.optionsButton.setOnClickListener {
                    showOptionsMenu(it, song)
                }

                holder.favoriteButton.setOnClickListener {
                    // Toggle favorite status
                    val newStatus = !song.isFavorite
                    onFavoriteClicked?.invoke(song, newStatus)

                    // Update the icon
                    if (newStatus) {
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                    } else {
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                    }
                }
            }
        }
    }

    private fun showOptionsMenu(view: View, song: SongModel) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.song_options_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_play -> {
                    // Play the song
                    val intent = Intent(view.context, PlaySongActivity::class.java).apply {
                        putExtra(PlaySongActivity.EXTRA_SONG_TITLE, song.name)
                        putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, song.artist)
                        putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, song.album)
                        putExtra(PlaySongActivity.EXTRA_DURATION, song.duration)
                        putExtra(PlaySongActivity.EXTRA_SONG_ID, song.id)
                        putExtra(PlaySongActivity.EXTRA_AUDIO_URL, song.url)
                        putExtra(PlaySongActivity.EXTRA_IMAGE_URL, song.image)
                    }
                    view.context.startActivity(intent)
                    true
                }
                R.id.action_add_to_playlist -> {
                    // Show add to playlist dialog
                    if (playlistHelper == null) {
                        playlistHelper = PlaylistDialogHelper(view.context)
                    }

                    if (viewModel != null) {
                        playlistHelper?.showAddToPlaylistDialog(viewModel!!, song.id)
                    }
                    true
                }
                R.id.action_share -> {
                    // Share song
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Check out this song!")
                        putExtra(Intent.EXTRA_TEXT, "Listen to ${song.name} by ${song.artist}")
                    }
                    view.context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun getItemCount(): Int = songs.size

    fun updateData(newSongs: List<SongModel>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }

    fun setViewModel(viewModel: MusicViewModel) {
        this.viewModel = viewModel
    }
}