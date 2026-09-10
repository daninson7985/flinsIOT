package com.example.myappeva

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Temporallmente deshabilitado para pruebas
        setContentView(R.layout.activity_splash)
        
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        
        lottieAnimationView.addLottieOnCompositionLoadedListener {
            lottieAnimationView.playAnimation()
        }
        
        lottieAnimationView.setAnimation(R.raw.splash_anim)
        lottieAnimationView.repeatCount = -1 // Infinito
        lottieAnimationView.speed = 1.5f
        lottieAnimationView.playAnimation()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Handler(Looper.getMainLooper()).postDelayed({
            // Cargamos el Activity principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Cerramos el Splash Activity
            finish()
        }, 7000) // Tiempo de espera de 7 segundos exactos
    }
}