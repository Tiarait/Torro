package dev.tiar.torro.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import dev.tiar.torro.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        val splash = AnimationUtils.loadAnimation(this, R.anim.splash)
        splash_title.startAnimation(splash)
        splash_subtitle.startAnimation(splash)

        val logoTimer = object : Thread() {
            override fun run() {
                try {
                    var logoTimer = 0
                    while (logoTimer < 4000) {
                        Thread.sleep(100)
                        logoTimer += 100
                    }
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.putExtra("Tag", "home")
                    startActivity(intent)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    finish()
                }
            }
        }
        logoTimer.start()
    }
}
