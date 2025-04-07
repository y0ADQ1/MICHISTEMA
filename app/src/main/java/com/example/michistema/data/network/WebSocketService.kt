package com.example.michistema.data.network

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.michistema.R
import com.example.michistema.ui.main.DeviceDetailAreneroActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebSocketService : Service() {


    private lateinit var webSocket: WebSocket
    private var proximityTime: Long = 0
    private var isCatPresent = false

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification("Servicio iniciado"))
        iniciarWebSocket()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun iniciarWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("ws://atenasoficial.com:3003")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("WebSocket conectado en servicio")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                procesarMensaje(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Error WebSocket en servicio: ${t.message}")
            }
        })
    }

    private fun procesarMensaje(mensaje: String) {
        try {
            val json = JSONObject(mensaje)
            val topic = json.getString("topic")
            val message = json.getString("message")
            val value = message.toFloatOrNull() ?: return

            when (topic) {
                "arenero-ultrasonic" -> handleProximity(value)
                "arenero-gas" -> handleGases(value)
                "arenero-temperatura" -> handleHumidity(value)
            }
        } catch (e: Exception) {
            println("Error al procesar JSON en servicio: ${e.message}")
        }
    }

    private fun handleProximity(distance: Float) {
        if (distance < 10) {
            if (!isCatPresent) {
                proximityTime = System.currentTimeMillis()
                isCatPresent = true
            } else if (System.currentTimeMillis() - proximityTime >= 5000) {
                sendNotification("Gato en arenero", "El gato ha estado en el arenero por 5 segundos.")
                proximityTime = 0
            }
        } else {
            isCatPresent = false
            proximityTime = 0
        }
    }

    private fun handleGases(gasLevel: Float) {
        val percentage = gasLevel / 1000 * 100
        when {
            percentage >= 100 -> sendNotification(
                "Limpieza urgente",
                "Nivel de gases al 100%, requiere limpieza urgente."
            )
            percentage >= 50 -> sendNotification(
                "Limpieza ligera",
                "Nivel de gases al $percentage%, requiere limpieza ligera."
            )
        }
    }

    private fun handleHumidity(humidity: Float) {
        when {
            humidity >= 100 -> sendNotification(
                "Limpieza urgente",
                "Humedad al 100%, requiere limpieza urgente."
            )
            humidity >= 50 -> sendNotification(
                "Limpieza ligera",
                "Humedad al $humidity%, requiere limpieza ligera."
            )
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "arenero_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones Arenero",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, DeviceDetailAreneroActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotification(message: String): Notification {
        val channelId = "arenero_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Servicio Arenero",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Servicio Arenero")
            .setContentText(message)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "Servicio destruido")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}