package com.example.appbardemo.ui.theme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R


class Mp3Adapter(private val context: Context, private val items: List<Mp3Items>) :
    RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder>(){

    class Mp3ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val mp3title : TextView=view.findViewById(R.id.mp3Title)
        val mp3artist : TextView=view.findViewById(R.id.mp3Subtitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mp3ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mp3_cutter_item, parent, false)
        return Mp3ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Mp3ViewHolder, position: Int) {
        val item=items[position]
        holder.mp3title.text=item.title
        holder.mp3artist.text=item.artist

        holder.itemView.setOnClickListener {
            val intent= Intent(context,Mp3EditorActivity::class.java)
            context.startActivity(intent)
        }

    }


}
