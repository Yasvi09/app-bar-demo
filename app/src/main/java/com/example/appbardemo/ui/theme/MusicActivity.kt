package com.example.appbardemo.ui.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.appbardemo.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val tabTitles = arrayOf("ALBUM", "SONGS", "ARTIST", "FOLDER", "PLAYLIST")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Set up the ViewPager with the adapter
        val pagerAdapter = MusicPagerAdapter(this, tabTitles.size)
        viewPager.adapter = pagerAdapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Set back button click listener
        findViewById<android.widget.ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}