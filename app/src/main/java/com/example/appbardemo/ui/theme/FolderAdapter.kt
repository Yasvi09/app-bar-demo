package com.example.appbardemo.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class FolderAdapter(
    private val items: List<MusicItem>
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderIcon: ImageView = itemView.findViewById(R.id.folderIcon)
        val folderName: TextView = itemView.findViewById(R.id.folderNameText)
        val itemCount: TextView = itemView.findViewById(R.id.folderItemCountText)
        val optionsButton: ImageView = itemView.findViewById(R.id.optionsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val item = items[position]

        holder.folderName.text = item.title
        holder.itemCount.text = item.subtitle
        holder.folderIcon.setImageResource(item.iconResId)

        holder.itemView.setOnClickListener {

        }

        holder.optionsButton.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = items.size
}