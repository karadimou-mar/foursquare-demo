package com.example.venueapp.models

import com.example.venueapp.models.main.Icon

data class Category(
    val icon: Icon,
    val id: String,
    val name: String,
    val pluralName: String,
    val primary: Boolean,
    val shortName: String
)