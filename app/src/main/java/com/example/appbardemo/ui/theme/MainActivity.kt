package com.example.appbardemo.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var fabLayout: FrameLayout
    private lateinit var curvedBottomNav: CurvedBottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabLayout = findViewById(R.id.fabContainer)
        fabLayout.setOnClickListener {
            showMenu()
        }

        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)

        curvedBottomNav.setNavColor(getColor(R.color.white))
        curvedBottomNav.setCornerRadius(40f)
        curvedBottomNav.setFabSize(200f)
        curvedBottomNav.setNotchCornerRadius(20f)
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

        // Find the music button using our helper method
        val musicButtonContainer = findButtonByText(bottomSheetView, "Music")
        musicButtonContainer?.setOnClickListener {
            // Launch the Music Activity
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
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

    // Helper method to find a button container by the text label
    private fun findButtonByText(rootView: View, buttonText: String): FrameLayout? {
        // Find all TextViews in the bottom sheet that match our text
        val matchingTextViews = findViewsWithText(rootView, buttonText)

        // For each matching TextView, find its diamond container
        for (textView in matchingTextViews) {
            // Find the diamond-shaped FrameLayout (the button container)
            // It should be rotated by 45 degrees
            val diamondContainer = findParentWithRotation(textView, 45f)
            if (diamondContainer is FrameLayout) {
                return diamondContainer
            }
        }

        return null
    }

    // Helper method to find all TextViews with specific text
    private fun findViewsWithText(view: View, text: String): List<TextView> {
        val result = mutableListOf<TextView>()

        // If this is a TextView with matching text, add it
        if (view is TextView && view.text.toString().trim().equals(text, ignoreCase = true)) {
            result.add(view)
        }

        // If this is a ViewGroup, recursively check all children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                result.addAll(findViewsWithText(view.getChildAt(i), text))
            }
        }

        return result
    }

    // Helper method to find a parent view with specific rotation
    private fun findParentWithRotation(view: View, rotation: Float): View? {
        var current: View? = view

        // Go up the view hierarchy
        while (current != null) {
            // Check if this view has the desired rotation
            if (current.rotation == rotation) {
                return current
            }

            // Move up to the parent
            val parent = current.parent
            current = if (parent is View) parent else null
        }

        return null
    }
}