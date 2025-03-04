package com.example.appbardemo.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.appbardemo.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var curvedBottomNav: CurvedBottomNavigationView
    private lateinit var fabContainer: FrameLayout
    private val tabTitles = arrayOf("ALBUM", "SONGS", "ARTIST", "FOLDER", "PLAYLIST")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)
        fabContainer = findViewById(R.id.fabContainer)

        val pagerAdapter = MusicPagerAdapter(this, tabTitles.size)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        setupBottomNavigation()

        setupClickListeners()
    }

    private fun setupBottomNavigation() {
        curvedBottomNav.setNavColor(getColor(R.color.grey))
        curvedBottomNav.setCornerRadius(40f)
        curvedBottomNav.setFabSize(200f)
        curvedBottomNav.setNotchCornerRadius(20f)
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.homeIcon).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        
        // Add settings icon click listener
        findViewById<ImageView>(R.id.settingsIcon).setOnClickListener {
            // Launch settings activity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Set FAB click listener
        fabContainer.setOnClickListener {
            showMenu()
        }
    }

    private fun showMenu() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val curvedTopSheet = bottomSheetView.findViewById<CurvedTopSheetView>(R.id.curvedTopSheetBackground)
        curvedTopSheet.setSheetColor(getColor(R.color.white))
        curvedTopSheet.setCornerRadius(150f)
        curvedTopSheet.setButtonSize(220f)
        curvedTopSheet.setNotchCornerRadius(20f)

        val downButtonContainer = bottomSheetView.findViewById<FrameLayout>(R.id.downButtonContainer)
        downButtonContainer.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.post {
            val parent = bottomSheetView.parent as View
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(parent)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        bottomSheetDialog.show()
    }
}