package com.example.michistema.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.michistema.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import android.util.Base64

class LimpiarAreneroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_limpiar_arenero)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            enviarMensaje()
        }
    }

    private fun enviarMensaje() {
        val client = OkHttpClient()
        val url = "http://189.145.66.247:18083/api/v5/publish"

        // Tu usuario y contraseña
        val username = "aae9de2837a66715"
        val password = "rOzl4UG7bSfHanTfM0raooVet8tbAdj9Cncm9AD0x8qZK"

        // Codificar en Base64 las credenciales
        val credentials = "$username:$password"
        val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        // Construir el cuerpo JSON
        val json = JSONObject().apply {
            put("payload_encoding", "plain")
            put("topic", "mover-motor")
            put("qos", 0)
            put("payload", "100")
            put("retain", false)
            put("properties", JSONObject().apply {
                put("payload_format_indicator", 0)
                put("message_expiry_interval", 0)
                put("response_topic", "some_other_topic")
                put("correlation_data", "string")
                put("user_properties", JSONObject().apply {
                    put("foo", "bar")
                })
                put("content_type", "text/plain")
            })
        }

        // Crear el cuerpo de la solicitud
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        // Construir la solicitud con encabezado Authorization
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Basic $encodedCredentials")  // Aquí agregamos el encabezado Authorization
            .post(requestBody)
            .build()

        // Enviar la solicitud
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error en la petición HTTP: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                println("Respuesta del servidor: ${response.body?.string()}")
            }
        })
    }
}
