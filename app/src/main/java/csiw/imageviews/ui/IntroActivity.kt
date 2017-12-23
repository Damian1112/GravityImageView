package csiw.imageviews.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.WindowManager
import csiw.android.GyroscopeObserver
import csiw.android.ImageViews
import csiw.imageviews.R


class IntroActivity : AppCompatActivity() {
    private var gyroscopeObserver: GyroscopeObserver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_intro)
        gyroscopeObserver = GyroscopeObserver()

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)) {
            Log.e("GYROSCOPE", "True")
        } else {
            startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
        }
        val imageViewsOne = findViewById<ImageViews>(R.id.image_view_one)
        imageViewsOne.setGyroscopeObserver(gyroscopeObserver)
        imageViewsOne.setOnLongClickListener {
            startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            false
        }
        imageViewsOne.isInvertScrollDirection = false
        imageViewsOne.setEnablePanoramaMode(true)
        imageViewsOne.setEnableScrollbar(true)
        gyroscopeObserver?.setMaxRotateRadian(01.0)
    }

    override fun onResume() {
        super.onResume()
        gyroscopeObserver?.register(this)
    }

    override fun onPause() {
        super.onPause()
        gyroscopeObserver?.unregister()
    }
}
