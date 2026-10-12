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

class Registrousuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrousuario)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnVolverRU).setOnClickListener {
            finish()
        }

        val etNombre = findViewById<EditText>(R.id.etNombreRU)
        val etApellidos = findViewById<EditText>(R.id.etApellidosRU)
        val etEmail = findViewById<EditText>(R.id.etEmailRU)
        val etPassword = findViewById<EditText>(R.id.etPasswordRU)
        val etConfirmarPassword = findViewById<EditText>(R.id.etConfirmarPasswordRU)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarRU)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmarPassword.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showError("Error", "Todos los campos son obligatorios")
                return@setOnClickListener
            }

            if (!nombre.matches(Regex("^[\\p{L}\\s]+$")) || !apellidos.matches(Regex("^[\\p{L}\\s]+$"))) {
                showError("Formato inválido", "Nombres y apellidos solo deben contener letras y espacios (se permiten tildes y la letra ñ)")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Formato inválido", "Por favor, ingresa un correo electrónico válido")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showError("Error", "Las contraseñas no coinciden")
                return@setOnClickListener
            }

            if (!isPasswordRobust(password)) {
                showError("Contraseña débil", "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial")
                return@setOnClickListener
            }

            // Persistencia
            val dbHelper = DatabaseHelper(this)

            if (dbHelper.isEmailExists(email)) {
                showError("Error", "Este correo electrónico ya está registrado")
                return@setOnClickListener
            }

            if (dbHelper.isNameExists(nombre, apellidos)) {
                showError("Error", "Este nombre y apellido ya están registrados")
                return@setOnClickListener
            }

            val newUser = User(nombre = nombre, apellidos = apellidos, email = email, password = password)
            val result = dbHelper.addUser(newUser)

            if (result != -1L) {
                // Éxito en el registro
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText("Usuario registrado correctamente")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        finish() // Vuelve al menú de gestión de usuarios
                    }
                    .show()
            } else {
                showError("Error", "El correo ya está registrado o hubo un error")
            }
        }
    }

    private fun isPasswordRobust(password: String): Boolean {
        val pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        return password.matches(Regex(pattern))
    }

    private fun showError(title: String, message: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(message)
            .show()
    }
}
