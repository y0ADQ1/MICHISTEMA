package com.example.michistema.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.michistema.databinding.ActivityHomePageBinding
import com.example.michistema.ui.adapter.EnvironmentAdapter
import com.example.michistema.ui.auth.AddEnvironmentActivity
import com.example.michistema.viewmodel.HomePageViewModel

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private lateinit var viewModel: HomePageViewModel
    private lateinit var adapter: EnvironmentAdapter

    private val addEnvironmentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val newEnvironment = data?.getStringExtra("new_environment")
            newEnvironment?.let {
                viewModel.addEnvironment(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)

        adapter = EnvironmentAdapter(emptyList()) { environment ->
            Toast.makeText(this, "Clicked: $environment", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewHomePage.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHomePage.adapter = adapter

        viewModel.environments.observe(this) { environments ->
            adapter.updateEnvironments(environments)
        }

        viewModel.navigateToAddEnvironment.observe(this) { navigate ->
            if (navigate == true) {
                val intent = Intent(this, AddEnvironmentActivity::class.java)
                addEnvironmentLauncher.launch(intent)
                viewModel.onNavigationHandled()
            }
        }

        binding.btnAddEnvirement.setOnClickListener {
            viewModel.onAddEnvironmentClicked()
        }
    }
}