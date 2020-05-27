package com.example.venueapp.models

import com.example.venueapp.models.main.LabeledLatLng

data class Location(
    val address: String,
    val cc: String,
    val city: String,
    val country: String,
    val crossStreet: String,
    val distance: Int,
    val formattedAddress: ArrayList<String>,
    val labeledLatLngs: ArrayList<LabeledLatLng>,
    val lat: Double,
    val lng: Double,
    val postalCode: String,
    val state: String
)