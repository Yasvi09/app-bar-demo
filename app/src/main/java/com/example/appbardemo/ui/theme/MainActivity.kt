package com.example.appbardemo.ui.theme

import android.content.Intent
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

        fabLayout = findViewById(R.id.fabContainer)
        fabLayout.setOnClickListener {
            showMenu()
        }

        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)

        curvedBottomNav.setNavColor(getColor(R.color.white))
        curvedBottomNav.setCornerRadius(40f)
        curvedBottomNav.setFabSize(220f)
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

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }

}