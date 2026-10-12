package com.example.myappeva

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etUsuario = findViewById<EditText>(R.id.editTextText)
        val etPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val btnIngresar = findViewById<Button>(R.id.button)
        val tvRegistrarme = findViewById<TextView>(R.id.textView2)
        val tvOlvideContra = findViewById<TextView>(R.id.textView3)

        // Botón Ingresar
        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Campos obligatorios")
                    .setContentText("Por favor, completa todos los campos")
                    .show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Formato inválido")
                    .setContentText("Por favor, ingresa un correo electrónico válido")
                    .show()
                return@setOnClickListener
            }

            // Verificación en Base de Datos
            val dbHelper = DatabaseHelper(this)
            val isValid = dbHelper.checkUser(usuario, password)

            if (isValid) {
                // Si pasa las validaciones, vamos al Menu Principal
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Bienvenido")
                    .setContentText("Ingreso exitoso")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        val intent = Intent(this, Menuprin::class.java)
                        startActivity(intent)
                    }
                    .show()
            } else {
                if (dbHelper.isEmailExists(usuario) && dbHelper.isUserBlocked(usuario)) {
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Cuenta bloqueada")
                        .setContentText("Su cuenta está bloqueada temporalmente debido a un proceso de recuperación de contraseña en curso.")
                        .show()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error de autenticación")
                        .setContentText("Usuario o contraseña incorrectos")
                        .show()
                }
            }
        }

        // Enlace a Registrarse
        tvRegistrarme.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        // Enlace a Recuperar Contraseña
        tvOlvideContra.setOnClickListener {
            val intent = Intent(this, Recuperacontra::class.java)
            startActivity(intent)
        }
    }
}
