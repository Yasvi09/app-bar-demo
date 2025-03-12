package com.example.appbardemo.ui.theme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var fabLayout: View
    private lateinit var curvedBottomNav: CurvedBottomNavigationView
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize theme manager before setting content view
        themeManager = ThemeManager.getInstance(this)
        themeManager.updateAppTheme() // Apply current theme

        setContentView(R.layout.activity_main)

        try {
            FirebaseApp.initializeApp(this)
            println("Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("MusicPlayerApp", "Failed to initialize Firebase: ${e.message}", e)
        }
        init()
    }

    private fun init(){
        fabLayout = findViewById(R.id.squareFab)

        // Apply theme color to FAB background
        themeManager.applyThemeToFabGradient(fabLayout)

        fabLayout.setOnClickListener {
            showMenu()
        }

        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)

        // Apply theme color to navigation
        val themeColor = themeManager.getThemeColor()
        curvedBottomNav.setNavColor(getColor(R.color.white))
        curvedBottomNav.setCornerRadius(40f)
        curvedBottomNav.setFabSize(200f)
        curvedBottomNav.setNotchCornerRadius(20f)

        findViewById<ImageView>(R.id.settingsIcon).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
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

        bottomSheetView.post {
            // Get all the diamond containers
            val diamondContainers = findDiamondContainers(bottomSheetView)

            // Apply theme to each diamond container
            for (container in diamondContainers) {
                themeManager.applyThemeToDiamondBorder(container)
            }
        }

        val downButtonContainer = bottomSheetView.findViewById<FrameLayout>(R.id.downButtonContainer)
        downButtonContainer.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        val musicButtonContainer = findButtonByText(bottomSheetView, "Music")
        musicButtonContainer?.setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        val privateButtonContainer = findButtonByText(bottomSheetView, "Private")
        privateButtonContainer?.setOnClickListener {
            val intent = Intent(this, PrivateActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        val adsButtonContainer = findButtonByText(bottomSheetView, "Remove Ads")
        adsButtonContainer?.setOnClickListener {
            val intent = Intent(this, RemoveAdsActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        val mp3CutterContainer = findButtonByText(bottomSheetView, "MP3 Cutter")
        mp3CutterContainer?.setOnClickListener {
            val intent = Intent(this, Mp3CutterActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        val themeButtonContainer = findButtonByText(bottomSheetView, "Theme")
        themeButtonContainer?.setOnClickListener {
            ColorPickerActivity.start(this)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.post{
            val parent = bottomSheetView.parent as View
            val behavior = BottomSheetBehavior.from(parent)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
        bottomSheetDialog.show()
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

    private fun findButtonByText(rootView: View, buttonText: String): FrameLayout? {
        val matchingTextViews = findViewsWithText(rootView, buttonText)

        for (textView in matchingTextViews) {
            val diamondContainer = findParentWithRotation(textView, 45f)
            if (diamondContainer is FrameLayout) {
                return diamondContainer
            }
        }

        return null
    }

    private fun findViewsWithText(view: View, text: String): List<TextView> {
        val result = mutableListOf<TextView>()

        if (view is TextView && view.text.toString().trim().equals(text, ignoreCase = true)) {
            result.add(view)
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                result.addAll(findViewsWithText(view.getChildAt(i), text))
            }
        }
        return result
    }

    private fun findParentWithRotation(view: View, rotation: Float): View? {
        var current: View? = view

        while (current != null) {
            if (current.rotation == rotation) {
                return current
            }
            val parent = current.parent
            current = if (parent is View) parent else null
        }

        return null
    }
}