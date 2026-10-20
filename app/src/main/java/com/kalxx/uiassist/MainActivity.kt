package com.kalxx.uiassist
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val bg = Color.rgb(10, 8, 18)
    private val panel = Color.rgb(25, 19, 39)
    private val purple = Color.rgb(174, 90, 255)
    private val white = Color.WHITE
    private val gray = Color.LTGRAY
    private lateinit var root: LinearLayout
    private val REQUEST_CODE_CAPTURE = 1001
    private val REQUEST_CODE_OVERLAY = 1002
    private var isTopOffset = true 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun background(color: Int, radius: Float = 18f) = GradientDrawable().apply { setColor(color); cornerRadius = radius; setStroke(1, purple) }

    private fun newRoot(): LinearLayout {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(18, 18, 18, 18); setBackgroundColor(bg) }
        setContentView(root)
        return root
    }

    private fun title(text: String) {
        root.addView(TextView(this).apply { this.text = text; textSize = 25f; setTextColor(purple); typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER; setPadding(8, 18, 8, 18) })
    }

    private fun label(text: String) {
        root.addView(TextView(this).apply { this.text = text; textSize = 15f; setTextColor(white); setPadding(8, 8, 8, 12) })
    }

    private fun button(text: String, action: () -> Unit) {
        root.addView(Button(this).apply { this.text = text; textSize = 15f; setTextColor(white); background = background(panel); isAllCaps = false; setPadding(12, 12, 12, 12); setOnClickListener { action() } }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) })
    }

    private fun showHome() {
        newRoot()
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(scroll)
        scroll.addView(content)
        title("UI ASSIST")
        content.addView(TextView(this).apply { text = "SYSTEM AUTOMATION TOOL"; textSize = 12f; gravity = Gravity.CENTER; setTextColor(gray); setPadding(8, 0, 8, 20) })

        fun homeButton(text: String, action: () -> Unit) {
            content.addView(Button(this).apply { this.text = text; textSize = 16f; isAllCaps = false; setTextColor(white); background = background(panel); setPadding(12, 18, 12, 18); setOnClickListener { action() } }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 7, 0, 7) })
        }

        homeButton("🎮  App Launcher") { showGameLauncher() }
        val targetBtn = Button(this).apply {
            text = "🎯 Anchor: ${if (isTopOffset) "TOP" else "BOTTOM"}"
            textSize = 16f; isAllCaps = false; setTextColor(white); background = background(panel); setPadding(12, 18, 12, 18)
            setOnClickListener { isTopOffset = !isTopOffset; text = "🎯 Anchor: ${if (isTopOffset) "TOP" else "BOTTOM"}" }
        }
        content.addView(targetBtn, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 7, 0, 7) })
        homeButton("🚀  START SCREEN ASSIST") { startAutomationEngine() }
        homeButton("📊  Device Monitoring") { showMonitoring() }
        homeButton("🎚️  Sensitivity") { showSensitivity() }
        homeButton("🔗  Activation Guide") { showGuide() }
    }

    private fun startAutomationEngine() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please allow overlay permission!", Toast.LENGTH_SHORT).show()
            startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), REQUEST_CODE_OVERLAY)
            return
        }
        if (GestureControlService.instance == null) {
            Toast.makeText(this, "Please enable Accessibility Service!", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }
        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CODE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY) {
            if (Settings.canDrawOverlays(this)) startAutomationEngine()
        } else if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK && data != null) {
            val intent = Intent(this, ScreenCaptureOverlay::class.java).apply { putExtra("resultCode", resultCode); putExtra("data", data); putExtra("isTopOffset", isTopOffset) }
            startForegroundService(intent)
            Toast.makeText(this, "Assist Engine Active!", Toast.LENGTH_LONG).show()
        }
    }

    private fun page(name: String) {
        newRoot()
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(scroll)
        scroll.addView(content)
        title(name)
        content.addView(Button(this).apply { text = "← Back to Home"; isAllCaps = false; setTextColor(white); background = background(panel); setOnClickListener { showHome() } })
        root = content
    }

    private fun showGameLauncher() {
        page("APP LAUNCHER")
        label("Select an app to launch.")
        button("Free Fire") { launchGame("com.dts.freefireth") }
        button("Free Fire MAX") { launchGame("com.dts.freefiremax") }
    }

    private fun launchGame(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) startActivity(intent)
        else Toast.makeText(this, "App not found.", Toast.LENGTH_LONG).show()
    }

    private fun showMonitoring() {
        page("DEVICE MONITORING")
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        label("Battery: ${if (battery >= 0) "$battery%" else "N/A"}")
        label("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
        button("Refresh") { showMonitoring() }
    }

    private fun showSensitivity() {
        page("SENSITIVITY")
        label("Slider for testing preference.")
        val seek = SeekBar(this).apply { max = 100; progress = 50 }
        root.addView(seek)
    }

    private fun showGuide() {
        page("ACTIVATION GUIDE")
        label("1. Press Start Screen Assist.\n2. Allow Overlay permission.\n3. Enable 'UI Automation Service' in Accessibility.\n4. Press Start again, then choose 'Start Now' to capture screen.\n5. Open your app.")
        button("Open Accessibility Settings") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
    }
}
