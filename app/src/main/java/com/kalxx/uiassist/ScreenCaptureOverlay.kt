package com.kalxx.uiassist
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import kotlin.concurrent.thread

class ScreenCaptureOverlay : Service() {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var overlayView: OverlayView? = null
    private var screenWidth = 0
    private var screenHeight = 0
    private var isTopOffset = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("resultCode", -1) ?: return START_NOT_STICKY
        val data = intent?.getParcelableExtra<Intent>("data") ?: return START_NOT_STICKY
        isTopOffset = intent.getBooleanExtra("isTopOffset", true)
        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, data)
        val metrics = resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        val density = metrics.densityDpi
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay("ScreenCapture", screenWidth, screenHeight, density, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader?.surface, null, null)
        setupOverlay()
        startProcessing()
        return START_STICKY
    }

    private fun setupOverlay() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = OverlayView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        windowManager.addView(overlayView, params)
    }

    private fun startProcessing() {
        thread {
            while (true) {
                val image = imageReader?.acquireLatestImage() ?: continue
                val rawTouchX = screenWidth / 2f + (Math.random() * 300 - 150).toFloat()
                val rawTouchY = screenHeight / 2f + (Math.random() * 300 - 150).toFloat()
                val finalY = if (isTopOffset) rawTouchY - 80f else rawTouchY + 40f
                val finalX = rawTouchX
                overlayView?.updateMarker(finalX, finalY)
                if (GestureControlService.instance != null) {
                    GestureControlService.instance?.simulateTouch(finalX, finalY)
                }
                image.close()
                Thread.sleep(50)
            }
        }
    }
    override fun onDestroy() {
        virtualDisplay?.release()
        mediaProjection?.stop()
        overlayView?.let { (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it) }
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}

class OverlayView(context: Context) : View(context) {
    private var markerX = 0f
    private var markerY = 0f
    private val paint = Paint().apply { color = Color.RED; style = Paint.Style.STROKE; strokeWidth = 5f }
    fun updateMarker(x: Float, y: Float) { markerX = x; markerY = y; postInvalidate() }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(markerX - 50, markerY - 100, markerX + 50, markerY + 100, paint)
    }
}
