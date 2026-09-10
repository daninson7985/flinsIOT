package com.example.myappeva

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Menuprin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menuprin)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGestion = findViewById<Button>(R.id.btnGestionUsuarios)
        val btnSensores = findViewById<Button>(R.id.btnVerSensores)
        val btnDesarrollador = findViewById<Button>(R.id.btnDatosDesarrollador)
        val btnVolver = findViewById<ImageButton>(R.id.btnVolverMenu)

        btnVolver.setOnClickListener {
            finish() // Cierra esta pantalla y vuelve al Login
        }

        btnGestion.setOnClickListener {
            val intent = Intent(this, Gestiondeusuario::class.java)
            startActivity(intent)
        }

        btnSensores.setOnClickListener {
            val intent = Intent(this, Datossensores::class.java)
            startActivity(intent)
        }

        btnDesarrollador.setOnClickListener {
            val intent = Intent(this, Datosdeldesaroyador::class.java)
            startActivity(intent)
        }
    }
}