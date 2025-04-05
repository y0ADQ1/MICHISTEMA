package com.example.michistema.utils

import android.util.Base64
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MessageSender {

    fun enviarMensaje(topic: String, payload: String, onResponse: (String) -> Unit, onError: (String) -> Unit) {
        val client = OkHttpClient()
        val url = "http://189.244.34.160:18083/api/v5/publish"

        // Tus credenciales
        val username = "aae9de2837a66715"
        val password = "rOzl4UG7bSfHanTfM0raooVet8tbAdj9Cncm9AD0x8qZK"

        // Codificar en Base64 las credenciales
        val credentials = "$username:$password"
        val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        // Construir el cuerpo JSON
        val json = JSONObject().apply {
            put("payload_encoding", "plain")
            put("topic", topic)  // Usar el topic dinámico
            put("qos", 0)
            put("payload", payload)  // Usar el payload dinámico
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
            .addHeader("Authorization", "Basic $encodedCredentials")
            .post(requestBody)
            .build()

        // Enviar la solicitud
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("Error en la petición HTTP: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onResponse("Respuesta del servidor: ${response.body?.string()}")
                } else {
                    onError("Error en la respuesta: ${response.code}")
                }
            }
        })
    }
}
