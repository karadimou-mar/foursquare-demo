package com.example.venueapp.models.details

data class BestPhoto(
    val createdAt: Int,
    val height: Int,
    val id: String,
    val prefix: String,
    val source: Source,
    val suffix: String,
    val visibility: String,
    val width: Int
)