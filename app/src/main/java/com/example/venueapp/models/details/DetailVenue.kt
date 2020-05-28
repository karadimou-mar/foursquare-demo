package com.example.venueapp.models.details

import android.os.Parcel
import android.os.Parcelable
import com.example.venueapp.models.Category
import com.example.venueapp.models.Location
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailVenue(
    //val attributes: Attributes,
    //val beenHere: BeenHere,
    val bestPhoto: BestPhoto,
    val canonicalUrl: String,
    val categories: List<Category>,
    val contact: Contact,
    //val createdAt: Int,
    val description: String,
    //val hereNow: HereNow,
    val hours: Hours,
    val id: String,
    //val inbox: Inbox,
    //val likes: Likes,
    //val listed: Listed,
    val location: Location,
    val name: String,
    //val page: Page,
    //val pageUpdates: PageUpdates,
    val photos: Photos?,
    //val phrases: List<Phrase>,
   // val popular: Popular,
    val rating: Double,
    val ratingColor: String,
    val ratingSignals: Int,
    val shortUrl: String,
    //val stats: Stats,
    val storeId: String,
    val timeZone: String,
    //val tips: TipsX,
    val url: String,
    //val venueChains: List<Any>,
    val verified: Boolean
): Parcelable