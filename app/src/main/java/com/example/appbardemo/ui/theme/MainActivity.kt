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
    private lateinit var fabLayout: FrameLayout
    private lateinit var curvedBottomNav: CurvedBottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        fabLayout = findViewById(R.id.fabContainer)
        fabLayout.setOnClickListener {
            showMenu()
        }

        curvedBottomNav = findViewById(R.id.curvedBottomNavBackground)

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

        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.post{
            val parent = bottomSheetView.parent as View
            val behavior = BottomSheetBehavior.from(parent)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
        bottomSheetDialog.show()
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