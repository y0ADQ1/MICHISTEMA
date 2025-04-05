package com.example.michistema.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceCategory(
    val name: String,
    val iconResId: Int,
    val layoutResId: Int,
    val categoryType: String,
    val environment: String? = null,
    val cleaningInterval: String? = null,
    val foodAmount: String? = null,
    val waterLevel: String? = null
) : Parcelable