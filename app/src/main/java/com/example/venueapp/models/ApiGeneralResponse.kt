package com.example.venueapp.models

import android.os.Parcel
import android.os.Parcelable
import com.example.venueapp.models.details.DetailResponse
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class ApiGeneralResponse<T>(
    val meta: Meta,
    val response: T
)