package com.example.myappeva

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog

class Creacontra : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creacontra)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        userEmail = intent.getStringExtra("email") ?: ""

        findViewById<ImageButton>(R.id.btnVolverCrea).setOnClickListener {
            finish()
        }

        val etNuevaContra = findViewById<EditText>(R.id.etNuevaContra)
        val etConfirmarContra = findViewById<EditText>(R.id.etConfirmarContra)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarContra)

        btnGuardar.setOnClickListener {
            val password = etNuevaContra.text.toString()
            val confirmPassword = etConfirmarContra.text.toString()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                showError("Campos vacíos", "Ambos campos de contraseña son obligatorios")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showError("Error", "Las contraseñas no coinciden")
                return@setOnClickListener
            }

            if (!isPasswordRobust(password)) {
                showError("Contraseña Débil", "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial")
                return@setOnClickListener
            }

            if (userEmail.isEmpty()) {
                showError("Error", "No se encontró el correo del usuario. Reinicie el proceso de recuperación.")
                return@setOnClickListener
            }

            // Guardar nueva contraseña y reactivar/desbloquear la cuenta en la BD
            val success = dbHelper.updatePasswordAndUnblock(userEmail, password)

            if (success) {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("¡Éxito!")
                    .setContentText("Contraseña actualizada correctamente. Su cuenta ha sido reactivada. Ya puede iniciar sesión.")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        finish() // Cierra la actividad y vuelve al login
                    }
                    .show()
            } else {
                showError("Error", "No se pudo actualizar la contraseña en la base de datos.")
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
