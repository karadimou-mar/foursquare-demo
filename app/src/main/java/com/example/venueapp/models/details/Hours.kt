package com.example.venueapp.models.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hours(
    val isLocalHoliday: Boolean,
    val isOpen: Boolean,
    val status: String
    //val timeframes: List<Timeframe>
): Parcelable