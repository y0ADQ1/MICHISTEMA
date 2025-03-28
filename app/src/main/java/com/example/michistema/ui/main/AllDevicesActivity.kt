package com.example.michistema.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.michistema.R
import com.example.michistema.ui.adapter.DeviceAdapter
import com.example.michistema.ui.viewmodel.DeviceViewModel

class AllDevicesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private val deviceViewModel: DeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_devices)

        recyclerView = findViewById(R.id.recyclerViewDevices)
        recyclerView.layoutManager = LinearLayoutManager(this)

        deviceAdapter = DeviceAdapter(emptyList())
        recyclerView.adapter = deviceAdapter

        deviceViewModel.getDevices()

        deviceViewModel.devices.observe(this, Observer { devices ->
            deviceAdapter.updateList(devices)
        })

        val backButton = findViewById<View>(R.id.volver)
        backButton.setOnClickListener {
            finish()
        }
    }
}
