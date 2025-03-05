package com.example.appbardemo.ui.theme

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appbardemo.R

class PrivateActivity : AppCompatActivity() {
    private val correctPassword = "5635"  // Change this to your desired password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)

        // Show password dialog immediately
        showPasswordDialog()
    }

    private fun showPasswordDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_password)
        dialog.setCancelable(false)

        val passwordDisplay = dialog.findViewById<TextView>(R.id.passwordDisplay)
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
        val enterButton = dialog.findViewById<Button>(R.id.btnEnter)  // Added enter button handling

        val enteredPassword = StringBuilder()

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (enteredPassword.length < 4) {
                    enteredPassword.append(index)
                    passwordDisplay.text = enteredPassword.toString()
                }
            }
        }

        deleteButton.setOnClickListener {
            if (enteredPassword.isNotEmpty()) {
                enteredPassword.deleteCharAt(enteredPassword.length - 1)
                passwordDisplay.text = enteredPassword.toString()
            }
        }

        // Check password on clicking Enter button
        enterButton.setOnClickListener {
            if (enteredPassword.toString() == correctPassword) {
                dialog.dismiss()
            } else {
                passwordDisplay.text = ""
                enteredPassword.clear()
            }
        }

        dialog.show()
    }
}