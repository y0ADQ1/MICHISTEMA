package com.example.michistema.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import com.example.michistema.R
import com.example.michistema.ui.auth.LoginActivity
import com.example.michistema.utils.PreferenceHelper.set
import com.example.michistema.utils.PreferenceHelper.get
import com.example.michistema.utils.PreferenceHelper



class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogout: ImageButton = findViewById(R.id.btn_logout)
        btnLogout.setOnClickListener {
            logout()
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
