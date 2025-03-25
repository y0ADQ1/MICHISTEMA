package com.example.michistema.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.michistema.R
import com.example.michistema.adapters.SandBoxAdapter
import com.example.michistema.databinding.ActivitySandsBoxBinding
import com.example.michistema.models.SandBoxItem

class SandsBoxActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySandsBoxBinding
    private lateinit var adapter: SandBoxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySandsBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            SandBoxItem("Arenero de: Milanes.", R.drawable.cat_sandbox_icon),
            SandBoxItem("Arenero de: Habsburgo.", R.drawable.cat_sandbox_icon),
            SandBoxItem("Arenero de: Kokita.", R.drawable.cat_sandbox_icon),
            SandBoxItem("Arenero de: Oz.", R.drawable.cat_sandbox_icon),
            SandBoxItem("Arenero de: Nata Jr.", R.drawable.cat_sandbox_icon),
            SandBoxItem("Agregar otro arenero", R.drawable.addition_icon)
        )

        adapter = SandBoxAdapter(items) { item ->
            if (item.name == "Agregar otro arenero") {
                val intent = Intent(this, AddSandBoxActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Seleccionaste ${item.name}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}
