package com.example.appbardemo.ui.theme

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var fabLayout: FrameLayout
    private lateinit var curvedBottomNav: CurvedBottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the FAB
        fabLayout = findViewById(R.id.fabContainer)
        fabLayout.setOnClickListener {
            showMenu()
        }

        // Initialize the curved bottom navigation background
        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)

        // Configure the navigation view
        curvedBottomNav.setNavColor(getColor(R.color.white))
        curvedBottomNav.setCornerRadius(40f)
        curvedBottomNav.setFabSize(220f) // Match this to your FAB size in dp
        curvedBottomNav.setNotchCornerRadius(20f) // Set the radius for the notch corners
    }

    private fun showMenu() {
        // Create and show the bottom sheet dialog
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        // Configure the curved top sheet background
        val curvedTopSheet = bottomSheetView.findViewById<CurvedTopSheetView>(R.id.curvedTopSheetBackground)
        curvedTopSheet.setSheetColor(getColor(R.color.white))
        curvedTopSheet.setCornerRadius(150f)
        curvedTopSheet.setButtonSize(220f) // Match this to your down button size in dp
        curvedTopSheet.setNotchCornerRadius(20f)

        // Set up the down button click listener to dismiss the dialog
        val downButtonContainer = bottomSheetView.findViewById<FrameLayout>(R.id.downButtonContainer)
        downButtonContainer.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }
}