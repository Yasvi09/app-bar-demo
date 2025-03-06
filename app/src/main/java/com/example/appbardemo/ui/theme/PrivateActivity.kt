package com.example.appbardemo.ui.theme

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R

class PrivateActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var correctPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        correctPassword = sharedPreferences.getString("password", null)

        showPasswordDialog()
    }

    private fun showPasswordDialog() {
        val dialog = Dialog(this, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_password)
        dialog.setCancelable(false)

        dialog.window?.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        val passwordPrompt = dialog.findViewById<TextView>(R.id.passwordPrompt)
        val passwordDisplay = dialog.findViewById<TextView>(R.id.passwordDisplay)
        passwordPrompt.text = if (correctPassword == null) "Set Password" else "Enter Password"

        val buttons = listOf(
            dialog.findViewById<Button>(R.id.btn0),
            dialog.findViewById<Button>(R.id.btn1),
            dialog.findViewById<Button>(R.id.btn2),
            dialog.findViewById<Button>(R.id.btn3),
            dialog.findViewById<Button>(R.id.btn4),
            dialog.findViewById<Button>(R.id.btn5),
            dialog.findViewById<Button>(R.id.btn6),
            dialog.findViewById<Button>(R.id.btn7),
            dialog.findViewById<Button>(R.id.btn8),
            dialog.findViewById<Button>(R.id.btn9)
        )
        val deleteButton = dialog.findViewById<Button>(R.id.btnDelete)

        val enteredPassword = StringBuilder()

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (enteredPassword.length < 4) {
                    enteredPassword.append(index)
                    passwordDisplay.text = enteredPassword.toString()

                    if (correctPassword != null && enteredPassword.toString() == correctPassword) {
                        dialog.dismiss()
                    } else if (enteredPassword.length == 4) {
                        if (correctPassword == null) {
                            sharedPreferences.edit().putString("password", enteredPassword.toString()).apply()
                            dialog.dismiss()
                        } else {
                            passwordDisplay.text = "Incorrect PIN"
                            enteredPassword.clear()

                            passwordDisplay.postDelayed({
                                passwordDisplay.text = ""
                            }, 1000)
                        }
                    }
                }
            }
        }

        deleteButton.setOnClickListener {
            if (enteredPassword.isNotEmpty()) {
                enteredPassword.deleteCharAt(enteredPassword.length - 1)
                passwordDisplay.text = if (enteredPassword.isEmpty()) "" else enteredPassword.toString()
            }
        }

        try {
            if (!isFinishing) {
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}