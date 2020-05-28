package com.example.venueapp.models.main

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LabeledLatLng(
    val label: String,
    val lat: Double,
    val lng: Double
): Parcelable