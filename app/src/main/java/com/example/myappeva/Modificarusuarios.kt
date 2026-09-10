package com.example.myappeva

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog

class Modificarusuarios : AppCompatActivity() {

    private var userId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modificarusuarios)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnVolverModificar).setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)

        val etNombre = findViewById<EditText>(R.id.etNombreMod)
        val etApellidos = findViewById<EditText>(R.id.etApellidosMod)
        val etEmail = findViewById<EditText>(R.id.etEmailMod)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarMod)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarMod)

        // Obtener datos del intent
        userId = intent.getIntExtra("USER_ID", -1)
        etNombre.setText(intent.getStringExtra("USER_NOMBRE"))
        etApellidos.setText(intent.getStringExtra("USER_APELLIDOS"))
        etEmail.setText(intent.getStringExtra("USER_EMAIL"))

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
                showError("Error", "Todos los campos son obligatorios")
                return@setOnClickListener
            }

            if (!nombre.matches(Regex("^[a-zA-Z\\s]+$")) || !apellidos.matches(Regex("^[a-zA-Z\\s]+$"))) {
                showError("Formato inválido", "Nombres y apellidos solo deben contener letras y espacios")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Formato inválido", "Por favor, ingresa un correo electrónico válido")
                return@setOnClickListener
            }

            if (dbHelper.isEmailExistsForOther(email, userId)) {
                showError("Error", "Este correo electrónico ya está en uso por otro usuario")
                return@setOnClickListener
            }

            if (dbHelper.isNameExistsForOther(nombre, apellidos, userId)) {
                showError("Error", "Este nombre y apellido ya están registrados")
                return@setOnClickListener
            }

            val updatedUser = User(id = userId, nombre = nombre, apellidos = apellidos, email = email, password = "")
            val result = dbHelper.updateUser(updatedUser)

            if (result > 0) {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText("Usuario actualizado correctamente")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        finish()
                    }
                    .show()
            } else {
                showError("Error", "No se pudo actualizar el usuario")
            }
        }

        btnEliminar.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Estás seguro?")
                .setContentText("Esta acción eliminará al usuario permanentemente")
                .setConfirmText("Sí, eliminar")
                .setCancelText("No, cancelar")
                .showCancelButton(true)
                .setConfirmClickListener { sDialog ->
                    val result = dbHelper.deleteUser(userId)
                    if (result > 0) {
                        sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        sDialog.setTitleText("Eliminado")
                        sDialog.setContentText("El usuario ha sido eliminado")
                        sDialog.setConfirmText("Aceptar")
                        sDialog.showCancelButton(false)
                        sDialog.setConfirmClickListener { 
                            it.dismissWithAnimation()
                            finish()
                        }
                    } else {
                        sDialog.dismissWithAnimation()
                        showError("Error", "No se pudo eliminar el usuario")
                    }
                }
                .show()
        }
    }

    private fun showError(title: String, message: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(message)
            .show()
    }
}