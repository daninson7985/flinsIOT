package com.example.myappeva

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Datosdeldesaroyador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datosdeldesaroyador)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnVolverDesa).setOnClickListener {
            finish()
        }

        // Configurar botones de redes sociales - Desarrollador 1
        val btnGithub = findViewById<ImageButton>(R.id.btnGithub1)
        val btnLinkedin = findViewById<ImageButton>(R.id.btnLinkedin1)

        btnGithub.setOnClickListener {
            openUrl("https://github.com/daninson7985")
        }

        btnLinkedin.setOnClickListener {
            openUrl("https://cl.linkedin.com/")
        }

        // Configurar botones de redes sociales - Desarrollador 2
        val btnGithub2 = findViewById<ImageButton>(R.id.btnGithub2)
        val btnLinkedin2 = findViewById<ImageButton>(R.id.btnLinkedin2)

        btnGithub2.setOnClickListener {
            openUrl("https://github.com/RBLInacap")
        }

        btnLinkedin2.setOnClickListener {
            openUrl("https://cl.linkedin.com/")
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
        }
    }
}
