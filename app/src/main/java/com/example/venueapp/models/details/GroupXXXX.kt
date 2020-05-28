package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupXXXX(
    val count: Int,
    val items: List<ItemXXXX>,
    val name: String,
    val type: String
): Parcelable