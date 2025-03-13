package com.example.appbardemo.ui.theme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R


class Mp3Adapter(
    private val context: Context,
    private var songs: List<SongModel>
) : RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder>() {

    class Mp3ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mp3title: TextView = view.findViewById(R.id.mp3Title)
        val mp3artist: TextView = view.findViewById(R.id.mp3Subtitle)
        val playIcon: ImageView = view.findViewById(R.id.playIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mp3ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mp3_cutter_item, parent, false)
        return Mp3ViewHolder(view)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: Mp3ViewHolder, position: Int) {
        val song = songs[position]
        holder.mp3title.text = song.name
        holder.mp3artist.text = song.artist

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Mp3EditorActivity::class.java).apply {
                putExtra("song_id", song.id)
                putExtra("song_name", song.name)
                putExtra("song_artist", song.artist)
                putExtra("song_url", song.url)
                putExtra("song_duration", song.duration)
            }
            context.startActivity(intent)
        }

        holder.playIcon.setOnClickListener {

            val intent = Intent(context, PlaySongActivity::class.java).apply {
                putExtra(PlaySongActivity.EXTRA_SONG_TITLE, song.name)
                putExtra(PlaySongActivity.EXTRA_ARTIST_NAME, song.artist)
                putExtra(PlaySongActivity.EXTRA_ALBUM_NAME, song.album)
                putExtra(PlaySongActivity.EXTRA_DURATION, song.duration)
                putExtra(PlaySongActivity.EXTRA_SONG_ID, song.id)
                putExtra(PlaySongActivity.EXTRA_AUDIO_URL, song.url)
                putExtra(PlaySongActivity.EXTRA_IMAGE_URL, song.image)
            }
            context.startActivity(intent)
        }
    }

    fun updateData(newSongs: List<SongModel>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }
}