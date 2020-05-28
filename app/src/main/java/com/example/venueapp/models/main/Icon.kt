package com.example.venueapp.models.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Icon(
    val prefix: String,
    val suffix: String
): Parcelable