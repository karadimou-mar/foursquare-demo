package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Source(
    val name: String,
    val url: String
): Parcelable