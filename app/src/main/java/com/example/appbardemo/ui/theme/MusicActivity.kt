package com.example.appbardemo.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.appbardemo.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var curvedBottomNav: CurvedBottomNavigationView
    private lateinit var fabContainer: View
    private lateinit var viewModel: MusicViewModel
    private lateinit var themeManager: ThemeManager
    private val tabTitles = arrayOf("ALBUM", "SONGS", "ARTIST", "FOLDER", "PLAYLIST")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        try {
            // Initialize ViewModel
            viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

            // Initialize views
            initializeViews()

            val pagerAdapter = MusicPagerAdapter(this, tabTitles.size)
            viewPager.adapter = pagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()

            setupClickListeners()
            setupBottomNavigation()

            tabLayout = findViewById(R.id.tabLayout)
            // Add this line:
            tabLayout.setSelectedTabIndicatorColor(ThemeManager.getInstance(this).getThemeColor())

        } catch (e: Exception) {
            // Log the exception and show a Toast message
            e.printStackTrace()
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
            // Safely finish the activity
            finish()
        }
    }

    private fun initializeViews() {
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)
        fabContainer = findViewById(R.id.squareFab)


        themeManager = ThemeManager.getInstance(this)
        // Apply theme color to FAB background
        themeManager.applyThemeToFabGradient(fabContainer)

    }

    private fun setupBottomNavigation() {
        curvedBottomNav.setNavColor(getColor(R.color.grey))
        curvedBottomNav.setCornerRadius(40f)
        curvedBottomNav.setFabSize(200f)
        curvedBottomNav.setNotchCornerRadius(20f)
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageView>(R.id.backButton)?.setOnClickListener {
            finish()
        }

        // Home button
        findViewById<ImageView>(R.id.homeIcon)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Settings button
        findViewById<ImageView>(R.id.settingsIcon)?.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // FAB
        fabContainer.setOnClickListener {
            showMenu()
        }

        // Search button
        findViewById<ImageView>(R.id.searchButton)?.setOnClickListener {
            Toast.makeText(this, "Search feature coming soon", Toast.LENGTH_SHORT).show()
        }

        // Menu button
        findViewById<ImageView>(R.id.menuButton)?.setOnClickListener {
            Toast.makeText(this, "Menu options coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMenu() {
        try {
            val bottomSheetDialog = BottomSheetDialog(this)
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

            val curvedTopSheet = bottomSheetView.findViewById<CurvedTopSheetView>(R.id.curvedTopSheetBackground)
            curvedTopSheet?.let {
                it.setSheetColor(getColor(R.color.white))
                it.setCornerRadius(150f)
                it.setButtonSize(220f)
                it.setNotchCornerRadius(20f)
            }

            bottomSheetView.post {
                // Get all the diamond containers
                val diamondContainers = findDiamondContainers(bottomSheetView)

                // Apply theme to each diamond container
                for (container in diamondContainers) {
                    themeManager.applyThemeToDiamondBorder(container)
                }
            }


            val downButtonContainer = bottomSheetView.findViewById<FrameLayout>(R.id.downButtonContainer)
            downButtonContainer?.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setContentView(bottomSheetView)

            bottomSheetView.post {
                val parent = bottomSheetView.parent as? View
                parent?.let {
                    val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
                    behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                    behavior.isDraggable = false
                }
            }

            bottomSheetDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error showing menu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findDiamondContainers(rootView: View): List<View> {
        val result = mutableListOf<View>()

        // If the root view is a ViewGroup, search through its children
        if (rootView is ViewGroup) {
            // Look for FrameLayouts with rotation == 45 and background == diamond_border
            for (i in 0 until rootView.childCount) {
                val child = rootView.getChildAt(i)

                // Check if this is a diamond container
                if (child is FrameLayout && child.rotation == 45f) {
                    // This is likely a diamond container
                    result.add(child)
                }

                // Recursively search in child view groups
                if (child is ViewGroup) {
                    result.addAll(findDiamondContainers(child))
                }
            }
        }

        return result
    }

}