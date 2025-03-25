package com.example.michistema.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import com.example.michistema.R
import com.example.michistema.ui.auth.LoginActivity
import com.example.michistema.ui.main.DrinkerActivity
import com.example.michistema.utils.PreferenceHelper.set
import com.example.michistema.utils.PreferenceHelper.get
import com.example.michistema.utils.PreferenceHelper



class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val btnLogout: Button = findViewById(R.id.btn_logout)
        btnLogout.setOnClickListener {
            logout()
        }

        val btnGetDrinkers: Button = findViewById(R.id.btn_get_drinkers)
        btnGetDrinkers.setOnClickListener {
            val intent = Intent(this, DrinkerActivity::class.java)
            startActivity(intent)
        }

        val btnGetDevices: Button = findViewById(R.id.btn_get_devices)
        btnGetDevices.setOnClickListener {
            val intent = Intent(this, AllDevicesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout() {
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["token"] = ""

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
