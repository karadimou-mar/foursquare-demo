package com.example.venueapp.models

data class ApiGeneralResponse<T>(
    val meta: Meta,
    val response: T
)