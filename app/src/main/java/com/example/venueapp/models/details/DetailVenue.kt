package com.example.venueapp.models.details

import android.os.Parcel
import android.os.Parcelable
import com.example.venueapp.models.Category
import com.example.venueapp.models.Location
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailVenue(
    val bestPhoto: BestPhoto,
    val canonicalUrl: String,
    val categories: List<Category>,
    val contact: Contact,
    val description: String,
    val hours: Hours,
    val id: String,
    val location: Location,
    val name: String,
    val photos: Photos?,
    val rating: Double,
    val ratingColor: String,
    val ratingSignals: Int,
    val shortUrl: String,
    val storeId: String,
    val timeZone: String,
    val url: String,
    val verified: Boolean
): Parcelable