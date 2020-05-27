package com.example.venueapp.models.main

data class Venue(
    val categories: ArrayList<Category>,
    val id: String,
    val location: Location,
    val name: String,
    val venuePage: VenuePage
)