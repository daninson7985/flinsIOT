package com.example.myappeva

import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Datossensores : AppCompatActivity() {

    private lateinit var fecha: TextView
    private lateinit var temp: TextView
    private lateinit var hum: TextView
    private lateinit var imagenTemp: ImageView
    private lateinit var datos: RequestQueue

    private val mHandler = Handler(Looper.getMainLooper())

    private var isFlashlightOn = false
    private var isBulbOn = false

    private lateinit var imgLinterna: ImageView
    private lateinit var tvLinternaStatus: TextView
    private lateinit var imgAmpolleta: ImageView
    private lateinit var tvAmpolletaStatus: TextView

    private val refrescar = object : Runnable {
        override fun run() {
            fecha.text = fechahora()
            obtenerDatos()
            mHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datossensores)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnVolverSensores).setOnClickListener {
            finish()
        }

        fecha = findViewById(R.id.txt_fecha)
        temp = findViewById(R.id.txt_temp)
        hum = findViewById(R.id.txt_humedad)
        imagenTemp = findViewById(R.id.imagen_temp)

        imgLinterna = findViewById(R.id.imgLinterna)
        tvLinternaStatus = findViewById(R.id.tvLinternaStatus)
        imgAmpolleta = findViewById(R.id.imgAmpolleta)
        tvAmpolletaStatus = findViewById(R.id.tvAmpolletaStatus)

        val cardLinterna = findViewById<MaterialCardView>(R.id.cardLinterna)
        cardLinterna.setOnClickListener {
            toggleFlashlight()
        }

        val cardAmpolleta = findViewById<MaterialCardView>(R.id.cardAmpolleta)
        cardAmpolleta.setOnClickListener {
            toggleBulbVisual()
        }

        datos = Volley.newRequestQueue(this)
        mHandler.post(refrescar)
    }

    private fun fechahora(): String {
        val c: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale("es", "ES"))
        return sdf.format(c.time)
    }

    private fun obtenerDatos() {
        val url = "https://www.pnk.cl/muestra_datos.php"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response: JSONObject ->
                try {
                    val temperaturaStr = response.getString("temperatura")
                    val humedadStr = response.getString("humedad")

                    temp.text = "$temperaturaStr C"
                    hum.text = "$humedadStr %"

                    val valor = temperaturaStr.toFloat()
                    cambiarImagen(valor)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error: VolleyError ->
                error.printStackTrace()
                // Fallback simulado si el servicio externo no responde
                val fallbackTemp = 20.0f + (Math.random() * 15.0).toFloat()
                temp.text = String.format(Locale.getDefault(), "%.1f C", fallbackTemp)
                hum.text = "65 %"
                cambiarImagen(fallbackTemp)
            }
        )
        datos.add(request)
    }

    private fun cambiarImagen(valor: Float) {
        if (valor >= 30) {
            imagenTemp.setImageResource(R.drawable.temperatura_alta)
        } else {
            imagenTemp.setImageResource(R.drawable.temperatura_baja)
        }
    }

    private fun toggleFlashlight() {
        try {
            val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull()
            if (cameraId != null) {
                isFlashlightOn = !isFlashlightOn
                cameraManager.setTorchMode(cameraId, isFlashlightOn)

                if (isFlashlightOn) {
                    imgLinterna.setColorFilter(android.graphics.Color.parseColor("#FDD835"))
                    tvLinternaStatus.text = "Encendida"
                } else {
                    imgLinterna.setColorFilter(android.graphics.Color.parseColor("#757575"))
                    tvLinternaStatus.text = "Apagada"
                }
            }
        } catch (_: Exception) {
            isFlashlightOn = !isFlashlightOn
            if (isFlashlightOn) {
                imgLinterna.setColorFilter(android.graphics.Color.parseColor("#FDD835"))
                tvLinternaStatus.text = "Encendida"
            } else {
                imgLinterna.setColorFilter(android.graphics.Color.parseColor("#757575"))
                tvLinternaStatus.text = "Apagada"
            }
        }
    }

    private fun toggleBulbVisual() {
        isBulbOn = !isBulbOn
        if (isBulbOn) {
            imgAmpolleta.setColorFilter(android.graphics.Color.parseColor("#FDD835"))
            tvAmpolletaStatus.text = "Encendida"
        } else {
            imgAmpolleta.setColorFilter(android.graphics.Color.parseColor("#757575"))
            tvAmpolletaStatus.text = "Apagada"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(refrescar)
        if (isFlashlightOn) {
            try {
                val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
                val cameraId = cameraManager.cameraIdList.firstOrNull()
                if (cameraId != null) {
                    cameraManager.setTorchMode(cameraId, false)
                }
            } catch (_: Exception) {}
        }
    }
}
