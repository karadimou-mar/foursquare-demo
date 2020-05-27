package com.example.venueapp.models.main

import com.example.venueapp.models.Category
import com.example.venueapp.models.Location

data class Venue(
    val categories: ArrayList<Category>,
    val id: String,
    val location: Location,
    val name: String,
    val venuePage: VenuePage
)