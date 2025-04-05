package com.example.michistema.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.michistema.R

class SpinnerDeviceTypeAdapter(
    context: Context,
    private val deviceTypes: Array<String>
) : ArrayAdapter<String>(context, R.layout.spinner_item_device_type, deviceTypes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_device_type, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = deviceTypes[position]
        textView.setTextColor(context.resources.getColor(android.R.color.black))
        return view
    }
}