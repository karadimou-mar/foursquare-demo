package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val createdAt: Int,
    val height: Int,
    val id: String,
    val prefix: String,
    //val source: SourceX,
    val suffix: String,
    //val user: UserXXX,
    val visibility: String,
    val width: Int
): Parcelable