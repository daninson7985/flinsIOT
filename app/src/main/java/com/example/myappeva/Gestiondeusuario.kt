package com.example.myappeva

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Gestiondeusuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestiondeusuario)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIngresar = findViewById<Button>(R.id.btnIngresarUsuario)
        val btnListar = findViewById<Button>(R.id.btnListarUsuarios)
        val btnVolver = findViewById<ImageButton>(R.id.btnVolverGestion)

        btnVolver.setOnClickListener {
            finish()
        }

        btnIngresar.setOnClickListener {
            val intent = Intent(this, Registrousuario::class.java)
            startActivity(intent)
        }

        btnListar.setOnClickListener {
            val intent = Intent(this, Listadousuario::class.java)
            startActivity(intent)
        }
    }
}