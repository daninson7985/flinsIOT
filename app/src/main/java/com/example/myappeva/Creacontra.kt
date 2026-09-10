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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creacontra)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

            // Éxito
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("¡Éxito!")
                .setContentText("Contraseña actualizada correctamente")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    finish() // Cierra la actividad
                }
                .show()
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