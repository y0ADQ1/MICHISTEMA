package com.example.michistema.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.michistema.databinding.ForgotPasswordActivityBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ForgotPasswordActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ForgotPasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Se ha enviado un correo a $email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
