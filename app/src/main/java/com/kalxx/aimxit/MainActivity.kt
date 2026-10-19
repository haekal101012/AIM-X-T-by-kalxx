package com.kalxx.aimxit

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private val purple = Color.rgb(174, 85, 255)
    private val background = Color.rgb(10, 7, 20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 32, 24, 32)
            setBackgroundColor(background)
        }

        val title = TextView(this).apply {
            text = "AIM X!T"
            textSize = 34f
            setTextColor(purple)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "by kalxx"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)

        addButton(root, "🎮  GAME LAUNCHER") {
            showPage("GAME LAUNCHER", "Pilih game yang ingin dimainkan.")
        }

        addButton(root, "🎯  CROSSHAIR") {
            showPage("CROSSHAIR", "Pengaturan pratinjau crosshair.")
        }

        addButton(root, "📊  MONITORING") {
            showPage("MONITORING", "Monitoring perangkat belum diaktifkan.")
        }

        addButton(root, "🎚️  SENSITIVITAS") {
            showPage("SENSITIVITAS", "Atur skala sensitivitas untuk referensi.")
        }

        addButton(root, "🔗  SHIZUKU / ADB") {
            showPage("PAIRING", "Ikuti panduan pairing sesuai versi Android.")
        }

        addButton(root, "🎬  VIDEO & MUSIK") {
            showPage("VIDEO & MUSIK", "Pemutar media akan ditambahkan.")
        }

        setContentView(root)
    }

    private fun addButton(
        parent: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(85, 35, 135))
            setOnClickListener { action() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            58
        ).apply {
            setMargins(0, 8, 0, 8)
        }

        parent.addView(button, params)
    }

    private fun showPage(title: String, description: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(background)
        }

        val heading = TextView(this).apply {
            text = title
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(purple)
            gravity = Gravity.CENTER
        }

        val detail = TextView(this).apply {
            text = description
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }

        val back = Button(this).apply {
            text = "KEMBALI"
            setOnClickListener { showHome() }
        }

        root.addView(heading)
        root.addView(detail)
        root.addView(back)

        setContentView(root)
    }
}
