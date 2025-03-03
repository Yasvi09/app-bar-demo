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
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.musicIcon)
        val title: TextView = itemView.findViewById(R.id.titleText)
        val subtitle: TextView = itemView.findViewById(R.id.subtitleText)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
        val optionsButton: ImageView = itemView.findViewById(R.id.optionsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.icon.setImageResource(item.iconResId)

        // Show favorite button only for songs
        if (type == "song") {
            holder.favoriteButton.visibility = View.VISIBLE
        } else {
            holder.favoriteButton.visibility = View.GONE
        }

        // Set click listeners
        holder.itemView.setOnClickListener {
            // Handle item click
        }

        holder.optionsButton.setOnClickListener {
            // Show options menu
        }

        holder.favoriteButton.setOnClickListener {
            // Toggle favorite status
        }
    }

    override fun getItemCount(): Int = items.size
}