package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BestPhoto(
    val createdAt: Int,
    val height: Int,
    val id: String,
    val prefix: String,
    val source: Source,
    val suffix: String,
    val visibility: String,
    val width: Int
): Parcelable