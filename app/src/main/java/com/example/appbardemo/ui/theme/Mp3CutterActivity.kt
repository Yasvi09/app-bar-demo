package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appbardemo.R

class Mp3CutterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: MusicViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3_cutter)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyText = findViewById(R.id.emptyText)

        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Mp3Adapter(this, emptyList())

        progressBar.visibility = View.VISIBLE

        viewModel.loadSongs()

        viewModel.songs.observe(this) { songs ->
            progressBar.visibility = View.GONE

            if (songs.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyText.visibility = View.GONE
                (recyclerView.adapter as Mp3Adapter).updateData(songs)
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            progressBar.visibility = View.GONE
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}


