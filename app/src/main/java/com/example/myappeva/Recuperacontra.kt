package com.example.myappeva

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import java.util.Locale
import kotlin.random.Random

class Recuperacontra : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var countDownTimer: CountDownTimer? = null
    private var currentEmail: String = ""
    private var generatedCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacontra)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        findViewById<ImageButton>(R.id.btnVolverRecu).setOnClickListener {
            finish()
        }

        val layoutEmailSection = findViewById<LinearLayout>(R.id.layoutEmailSection)
        val layoutCodeSection = findViewById<LinearLayout>(R.id.layoutCodeSection)
        val etEmail = findViewById<EditText>(R.id.etEmailRecuperar)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperar)

        val etCodigo = findViewById<EditText>(R.id.etCodigoRecuperacion)
        val btnVerificar = findViewById<Button>(R.id.btnVerificarCodigo)
        val btnReenviar = findViewById<Button>(R.id.btnReenviarCodigo)
        val tvTimer = findViewById<TextView>(R.id.tvTimer)

        btnRecuperar.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                showError("Error", "El campo de correo no puede estar vacío")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Formato inválido", "Por favor, ingresa un correo electrónico válido")
                return@setOnClickListener
            }

            if (!dbHelper.isEmailExists(email)) {
                showError("Correo no encontrado", "Este correo electrónico no está registrado en el sistema")
                return@setOnClickListener
            }

            currentEmail = email
            sendRecoveryCodeAndLock(email, layoutEmailSection, layoutCodeSection, tvTimer)
        }

        btnVerificar.setOnClickListener {
            val code = etCodigo.text.toString().trim()
            if (code.length != 4) {
                showError("Código incompleto", "Por favor, ingresa el código de 4 dígitos")
                return@setOnClickListener
            }

            val result = dbHelper.verifyRecoveryCode(currentEmail, code)
            when (result) {
                0 -> {
                    // Success
                    countDownTimer?.cancel()
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("¡Código verificado!")
                        .setContentText("Proceda a crear su nueva contraseña.")
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            val intent = Intent(this, Creacontra::class.java).apply {
                                putExtra("email", currentEmail)
                            }
                            startActivity(intent)
                            finish()
                        }
                        .show()
                }
                1 -> {
                    showError("Código expirado", "El código ha expirado (más de 1 minuto). Solicite uno nuevo.")
                }
                2 -> {
                    showError("Código incorrecto", "El código ingresado no es válido.")
                }
                3 -> {
                    showError("Error", "Usuario no encontrado.")
                }
            }
        }

        btnReenviar.setOnClickListener {
            sendRecoveryCodeAndLock(currentEmail, layoutEmailSection, layoutCodeSection, tvTimer)
        }
    }

    private fun sendRecoveryCodeAndLock(email: String, emailSection: View, codeSection: View, tvTimer: TextView) {
        // Generar código aleatorio de 4 dígitos (ej. 1000 a 9999)
        generatedCode = String.format(Locale.getDefault(), "%04d", Random.nextInt(1000, 10000))
        val timestamp = System.currentTimeMillis()

        // Bloquear cuenta y guardar código con timestamp en BD
        dbHelper.initiateRecovery(email, generatedCode, timestamp)

        // Mostrar sección de código
        emailSection.visibility = View.GONE
        codeSection.visibility = View.VISIBLE

        // Iniciar temporizador de 1 minuto (60000 ms)
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                tvTimer.text = String.format(Locale.getDefault(), "Tiempo restante: 00:%02d", seconds)
            }

            override fun onFinish() {
                tvTimer.text = "Tiempo agotado. Solicite un nuevo código."
                showError("Expirado", "El código de recuperación ha expirado.")
            }
        }.start()

        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Código enviado")
            .setContentText("Se ha enviado un código a $email (Simulación PHPMailer).\nCódigo de prueba: $generatedCode")
            .show()
    }

    private fun showError(title: String, message: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(message)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
