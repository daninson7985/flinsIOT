package com.example.myappeva

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Listadousuario : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listadousuario)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnVolverListado).setOnClickListener {
            finish()
        }

        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddUser)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, Registrousuario::class.java))
        }

        dbHelper = DatabaseHelper(this)
        val users = dbHelper.getAllUsers()

        val rvUsuarios = findViewById<RecyclerView>(R.id.rvUsuarios)
        rvUsuarios.layoutManager = LinearLayoutManager(this)

        adapter = UserAdapter(users) { user ->
            val intent = Intent(this, Modificarusuarios::class.java).apply {
                putExtra("USER_ID", user.id)
                putExtra("USER_NOMBRE", user.nombre)
                putExtra("USER_APELLIDOS", user.apellidos)
                putExtra("USER_EMAIL", user.email)
            }
            startActivity(intent)
        }
        rvUsuarios.adapter = adapter

        val svBuscar = findViewById<SearchView>(R.id.svBuscar)
        svBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Refrescar lista al volver de modificar
        val users = dbHelper.getAllUsers()
        adapter.updateData(users)
    }
}