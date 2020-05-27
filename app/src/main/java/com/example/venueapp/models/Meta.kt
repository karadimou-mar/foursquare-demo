package com.example.venueapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meta(
    val code: Int,
    val requestId: String
): Parcelable