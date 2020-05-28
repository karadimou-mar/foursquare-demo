package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    val count: Int,
    val items: List<Item>?,
    val name: String,
    val type: String
): Parcelable