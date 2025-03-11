package com.example.appbardemo

import android.app.Application
import com.example.appbardemo.ui.theme.ThemeManager
import com.google.firebase.FirebaseApp

class MusicPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Initialize ThemeManager
        val themeManager = ThemeManager.getInstance(this)
        themeManager.updateAppTheme()
    }
}