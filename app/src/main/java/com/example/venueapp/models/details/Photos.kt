package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photos(
    val count: Int,
    val groups: List<Group>?
): Parcelable