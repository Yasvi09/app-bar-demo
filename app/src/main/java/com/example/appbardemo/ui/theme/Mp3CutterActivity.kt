package com.example.appbardemo.ui.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class Mp3CutterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3_cutter)

        recyclerView = findViewById(R.id.recyclerView)

        val mp3Items = listOf(
            Mp3Items("Song Title 1", "Artist 1"),
            Mp3Items("Song Title 2", "Artist 2"),
            Mp3Items("Song Title 3", "Artist 3"),
            Mp3Items("Song Title 4", "Artist 4"),
            Mp3Items("Song Title 5", "Artist 5")
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Mp3Adapter(this,mp3Items)

    }
}


